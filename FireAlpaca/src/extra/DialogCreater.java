package extra;

import scene.LoginScene;

import com.example.base.BaseScene;
import com.example.manager.ResourcesManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;


public class DialogCreater extends DialogFragment {
	public interface DialogListener {
		public void onDifficultySelected(int difficulty);
	}

	private DialogListener mListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the
			// host
			mListener = (DialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement NoticeDialogListener");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(ResourcesManager.getInstance().activity);
		
		String s = null;
		savedInstanceState.getString("error", s);
		builder.setTitle(s);
		// Create the AlertDialog object and return it
		return builder.create();
	}
}
