package com.nightmare.SafeStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FileBrowser extends ListActivity {

	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}

	private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
	private List<String> directoryEntries = new ArrayList<String>();
	private File currentDirectory = new File("/");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// setContentView() gets called within the next line,
		// so we do not need it here.
		browseToRoot();
	}

	public static void browseMe(Context context) {
		Intent intent = new Intent(context, StorageActivity.class);
		context.startActivity(intent);
	}

	/**
	 * This function browses to the root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/"));
	}

	/**
	 * This function browses up one level according to the field:
	 * currentDirectory
	 */
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}

	private void browseTo(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {
			OnClickListener okButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Lets start an intent to View the file, that was
					// clicked...
					// TODO Opens file, but fails on some/all?
					// Intent myIntent = new Intent(
					// android.content.Intent.ACTION_VIEW,
					// Uri.parse("file://" + aDirectory.getAbsolutePath()));
					// startActivity(myIntent);
					StorageActivity.watchPath = currentDirectory.getAbsolutePath();
					toastify(StorageActivity.watchPath);
					Intent myIntent = new Intent(FileBrowser.this,
							StorageActivity.class);
					FileBrowser.this.startActivity(myIntent);
				}
			};
			OnClickListener cancelButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					// Do nothing
				}
			};
			new AlertDialog.Builder(this)
					.setTitle("Question")
					.setMessage(
							"Do you want to set " + aDirectory.getName() +"'s folder as your path?")
					.setPositiveButton("Yes", okButtonListener)
					.setNegativeButton("No", cancelButtonListener).show();
		}
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();

		// Add the "." and the ".." == 'Up one level'
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.directoryEntries.add("Set Path Here");

		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add("..");

		switch (this.displayMode) {
		case ABSOLUTE:
			for (File file : files) {
				this.directoryEntries.add(file.getPath());
			}
			break;
		case RELATIVE: // On relative Mode, we have to add the current-path to
						// the beginning
			int currentPathStringLenght = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {
				this.directoryEntries.add(file.getAbsolutePath().substring(
						currentPathStringLenght));
			}
			break;
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.file_row, this.directoryEntries);

		this.setListAdapter(directoryList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		String position2 = (String) l.getItemAtPosition(position);

		String selectedFileString = position2;

		if (selectedFileString.equals("Set Path Here")) {
			StorageActivity.watchPath = this.currentDirectory.getAbsolutePath();
			toastify(StorageActivity.watchPath);
			Intent myIntent = new Intent(FileBrowser.this,
					StorageActivity.class);
			FileBrowser.this.startActivity(myIntent);
			// this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals("..")) {
			this.upOneLevel();
		} else if (selectedFileString.equals("root")) {
			toastify("root is n/a");
		} else {
			File clickedFile = null;
			switch (this.displayMode) {
			case RELATIVE:

				clickedFile = new File(position2);
				break;
			case ABSOLUTE:

				clickedFile = new File(position2);
				break;
			}
			if (clickedFile != null) {

				this.browseTo(clickedFile);
			}
		}
	}

	public void toastify(String toastText) {
		Context context = getApplicationContext();
		CharSequence text = toastText;
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

}