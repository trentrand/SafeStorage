package com.nightmare.SafeStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.Session.AccessType;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StorageActivity extends Activity {

	public static Set<String> lines1;
	public static Set<String> lines2;
	public static Set<String> symmetricDifference;
	public static int numberOfLines1, numberOfLines2;
	// TODO
	public static String toString = "Trentmrand@gmail.com";
	public static int watchNumber = 1;
	public static String file1Path, file2Path, watchPath;
	public static File checkFile1, checkFile2;

	/** Click to make a SnapShot **/
	private Button snapShot;

	/** Click to set a path for snapshots **/
	private Button setPath;

	/** Click to set a path for snapshots **/
	private Button setPathToPics;

	/** Click to set a path for downloads **/
	private Button setPathToDownloads;

	// private EditText editEmail;

	/** Alarm to check for comparisson of file **/
	AlarmManager am;

	/** Button to instantly compare **/
	private Button compareNow;
	Process p;
	private EditText editEmail;

	/** Email Validator Pattern **/
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	private Button makeNewFile;
	private Button deleteTestFile;

	// DROPBOX STUFF
	/** DropBox Login **/
	final static private String APP_KEY = "natzhqh3j1meluj";
	/** DropBox Password **/
	final static private String APP_SECRET = "s9j6nrqgya38o0x";
	/** DropBox Access Type **/
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	// DROPBOX In the class declaration section:
	/** DropBox API object **/
	private DropboxAPI<AndroidAuthSession> mDBApi;
	private boolean mLoggedIn;
	private String TAG = "SafeStorage";
	private Button authButton;
	private boolean useGmail = true;
	private boolean useDropbox = true;

	// You don't need to change these, leave them alone.
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// DROPBOX
		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();
		mDBApi = new DropboxAPI<AndroidAuthSession>(session);

		// Basic Android widgets
		setContentView(R.layout.main);

		checkAppKeySetup();
		addButtonListener();
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		setRepeatingAlarm();

		// Display the proper UI state if logged in or not
		setLoggedIn(mDBApi.getSession().isLinked());
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mDBApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);
			} catch (IllegalStateException e) {
				toastify("Couldn't authenticate with Dropbox:"
						+ e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
	}

	public void uploadWithDropbox(String filePath) {
		// Uploading content.
		// TODO
	}

	public void giveRootAccess() {
		try {
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");

			// Attempt to write a file to a root-only
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			try {
				p.waitFor();
				if (p.exitValue() != 255) {
					// TODO Code to run on success
					toastify("Root Access Granted");
				} else {
					// TODO Code to run on unsuccessful
					// toastify("Device is not rooted");
				}
			} catch (InterruptedException e) {
				// TODO Code to run in interrupted exception
				// toastify("Device is not rooted");
			}
		} catch (IOException e) {
			// TODO Code to run in input/output exception
			// toastify("Device is not rooted");
		}

	}

	// public void setOneTimeAlarm() {
	// Intent intent = new Intent(this, TimeAlarm.class);
	// PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	// intent, PendingIntent.FLAG_ONE_SHOT);
	// am.set(AlarmManager.RTC_WAKEUP,
	// System.currentTimeMillis() + (5 * 1000), pendingIntent);
	// }

	public void setRepeatingAlarm() {
		Intent intent = new Intent(this, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				(10 * 1000), pendingIntent);
	}

	public void toastify(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	public void addButtonListener() {
		// TODO Watch Path Button
		this.setPath = (Button) this.findViewById(R.id.watchPath);
		this.setPath.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toastify("- Choose The Directory To Watch -");
				Intent myIntent = new Intent(StorageActivity.this,
						FileBrowser.class);
				StorageActivity.this.startActivity(myIntent);
			}
		});
		// TODO Snapshot Button
		this.snapShot = (Button) this.findViewById(R.id.snapShot);
		this.snapShot.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (watchPath != null) {
					try {
						makeWatchFile();
						toastify("SnapShot " + watchNumber
								+ " has been created.");
					} catch (IOException e) {
						toastify("Failed to create snapshot.");
						e.printStackTrace();
					}
				} else {
					Intent snapshot = new Intent(StorageActivity.this,
							FileBrowser.class);
					StorageActivity.this.startActivity(snapshot);
					toastify("You must set watch path first!");
				}
			}
		});
		// TODO Set Path To Pictures Button
		this.setPathToPics = (Button) this.findViewById(R.id.setToPicsButton);
		this.setPathToPics.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				watchPath = "/mnt/sdcard/dcim";
				toastify("Watch Path Set to Pictures!");
			}
		});
		// TODO Authorize Dropbox with device, opens login screen
		this.authButton = (Button) this.findViewById(R.id.auth_button);
		this.authButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mLoggedIn) {
					logOut();
				} else {
					// Start the remote authentication
					mDBApi.getSession().startAuthentication(
							StorageActivity.this);
				}
			}
		});
		// TODO Set Path To Pictures Button
		this.setPathToDownloads = (Button) this
				.findViewById(R.id.setToDownloads);
		this.setPathToDownloads.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				watchPath = "/mnt/sdcard/download";
				toastify("Watch Path Set to Downloads!");

			}
		});
		// TODO Create a new file for testing
		this.makeNewFile = (Button) this.findViewById(R.id.newFileInPath);
		this.makeNewFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toastify(watchPath + "/testfile.txt");
				File newTestFile = new File(watchPath + "/testfile.txt");
				try {
					BufferedWriter writter = new BufferedWriter(new FileWriter(
							newTestFile));
					writter.write("Test File Created by Safe Storage.");
					writter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Print out the name of files in the directory
				// System.out.println(files[index].toString());
			}
		});
		// TODO Delete Test File
		this.deleteTestFile = (Button) this.findViewById(R.id.deleteTestFile);
		this.deleteTestFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				File testFileToDelete = new File(watchPath + "/testfile.txt");
				testFileToDelete.delete();
				toastify("Test File Deleted");
			}
		});
		// TODO Create emailEdit object for email address box
		editEmail = (EditText) this.findViewById(R.id.emailAddress);
		// TODO Compare File 1 & 2 now
		this.compareNow = (Button) this.findViewById(R.id.compareNow);
		this.compareNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// toastify("Checking if files are comparable");
				// if (checkIfEmailEntered()) {
				startComparison();
			}
			// }
		});

	}

	public static void makeWatchFile() throws IOException {
		// File directory = new File("c:\\Users\\trent_rand\\Pictures\\");
		if (watchPath != null) {

			File watchFile = new File(watchPath);

			File[] files = watchFile.listFiles();
			// TODO
			File file = new File("/sdcard/file" + watchNumber + ".txt");
			// File file = new File("file" + watchNumber + ".txt");

			BufferedWriter writter = new BufferedWriter(new FileWriter(file));
			for (int index = 0; index < files.length; index++) {
				writter.write(files[index].toString());
				writter.newLine();
				// Print out the name of files in the directory
				// System.out.println(files[index].toString());
			}
			watchNumber = (watchNumber == 1) ? 2 : 1;

			if (watchNumber == 1) {
				file1Path = file.getAbsolutePath();
			} else {
				file2Path = file.getAbsolutePath();
			}
			// setClipboardData(file.getAbsolutePath());
			writter.close();
		} else {
			System.out.println("Watch Path Not Set");
		}
		// System.out.println("File 1 path: " + file1Path);
		// System.out.println("File 2 path: " + file2Path);
	}

	public void startComparison() {
		if (checkIfEmailValid()) {
			if (checkIfComparable()) {
				deleteDuplicates();
			} else {
				toastify("Watch Files Not Comparable");
			}
		} else {
			toastify("Email Not Valid");
		}
	}

	public static boolean checkIfComparable() {
		// TODO
		checkFile1 = new File("/mnt/sdcard/file1.txt");
		checkFile2 = new File("/mnt/sdcard/file2.txt");
		// checkFile1 = new File("C:/Workspace/Stalker-Test/file1.txt");
		// checkFile2 = new File("C:/Workspace/Stalker-Test/file2.txt");
		if (checkFile1.exists() && checkFile2.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkIfEmailValid() {
		if (editEmail.getText().toString() != "") {
			Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher matcher = pattern.matcher(editEmail.getText().toString());
			return matcher.matches();
		}
		return false;
	}

	public void deleteDuplicates() {
		List<String> linesList1 = new ArrayList<String>();
		List<String> linesList2 = new ArrayList<String>();

		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream1 = new FileInputStream(checkFile1);
			// Get the object of DataInputStream
			DataInputStream in1 = new DataInputStream(fstream1);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
			FileInputStream fstream2 = new FileInputStream(checkFile2);
			// Get the object of DataInputStream
			DataInputStream in2 = new DataInputStream(fstream2);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

			// String[] lineString1 = new String[numberOfLines1];
			// String[] lineString2 = new String[numberOfLines2];
			String line1 = null;
			while ((line1 = br1.readLine()) != null) {
				linesList1.add(line1);
			}
			System.out.println(linesList1.toString());
			// TODO Seperate readers
			String line2 = null;
			while ((line2 = br2.readLine()) != null) {
				linesList2.add(line2);
			}
			System.out.println(linesList2.toString());
			// for (int x = 0; x < numberOfLines1; x++) {
			// lineString1[x] = br1.readLine();
			// System.out.println(br1.readLine());
			// }
			//
			// for (int y = 0; y < numberOfLines2; y++) {
			// lineString2[y] = br2.readLine();
			// }
			in1.close();
			in2.close();
			br1.close();
			br2.close();

			Set<String> notInSet1 = new HashSet<String>(linesList2);
			notInSet1.removeAll(linesList1);
			Set<String> notInSet2 = new HashSet<String>(linesList1);
			notInSet2.removeAll(linesList2);

			// The symmetric difference is the concatenation of the two
			// individual differences
			symmetricDifference = new HashSet<String>(notInSet1);
			symmetricDifference.addAll(notInSet2);

			System.out.println(symmetricDifference);

			// toastify("Making file with diffrences");
			ArrayList<String> diffPath = new ArrayList<String>();

			List<String> symlist = new ArrayList<String>(symmetricDifference);
			Object[] objects = symlist.toArray();

			for (int i = 0; i < objects.length; i++) {
				Object object = objects[i];
				// diffPath.add(watchPath + "/" + object);
				diffPath.add(object + "");
				System.out.println(diffPath.get(i));
			}

			// Intent sharingIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			// sharingIntent.putExtra(Intent.EXTRA_EMAIL,
			// new String[] { toString });
			// sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
			// "New Files from Safe Storage");
			// sharingIntent.setType("text/*");
			// ArrayList<Uri> uris = new ArrayList<Uri>();
			//
			// for (int diffs = 0; diffs < diffPath.size(); diffs++) {
			// Uri uriDiff = Uri.parse("file://" + diffPath.get(diffs));
			// uris.add(uriDiff);
			// }
			//
			// sharingIntent
			// .putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			// // startActivity(Intent.createChooser(sharingIntent,
			// // "Store Safely Using"));
			// startActivity(sharingIntent);
			// toastify("Your Files Are Being Prepared To Store");
			if (useGmail) {
				try {
					toastify("Your Files Have Been Safely Stored!");
					GMailSender sender = new GMailSender("androidsafestorage",
							"nightmareappssecure");
					for (int diffs = 0; diffs < diffPath.size(); diffs++) {
						sender.addAttachment(
								diffPath.get(diffs),
								"Safe Storage has automatically sent "
										+ diffPath.size()
										+ " files that have been recently added to your phone.");
					}
					sender.sendMail(
							"New Content Safely Stored By Safe Storage",
							"Attached are your files stored safely by Safe Storage.",
							"SafeStorage@nightmareapps.com", editEmail
									.getText().toString());
				} catch (Exception e) {
					System.out.println("Email not sent!");
					Log.e("SendMail", e.getMessage(), e);
				}
			}
			if (useDropbox) {
				for (int diffs = 0; diffs < diffPath.size(); diffs++) {
					// diffPath.get(diffs)
					FileInputStream uploadStream = new FileInputStream(
							diffPath.get(diffs));
					mDBApi.putFile("/testing.txt", uploadStream, 1000, null,
							null);
				}

			}

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Displays some useful info about the account, to demonstrate that we've
	 * successfully logged in
	 * 
	 * @param account
	 */
	public void displayAccountInfo(DropboxAPI.Account account) {
		if (account != null) {
			String info = "Name: " + account.displayName + "\n"
					+ "Referral Link: " + account.referralLink + "\n"
					+ "User ID: " + account.uid + "\n" + "Country: "
					+ account.country + "\n" + "Quota: " + account.quota;
		}
	}

	private void logOut() {
		// Remove credentials from the session
		mDBApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		setLoggedIn(false);
	}

	/**
	 * Convenience function to change UI state based on being logged in
	 */
	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
		// TODO
		if (loggedIn) {
		} else {
		}
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (APP_KEY.startsWith("CHANGE") || APP_SECRET.startsWith("CHANGE")) {
			toastify("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
			finish();
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			toastify("URL scheme in your app's "
					+ "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the "
					+ "scheme: " + scheme);
			finish();
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 * 
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],
					stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
					accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}
}