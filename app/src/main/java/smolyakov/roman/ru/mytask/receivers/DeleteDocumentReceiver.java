package smolyakov.roman.ru.mytask.receivers;

import java.io.File;
import java.util.ArrayList;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import smolyakov.roman.ru.mytask.objects.AppContext;
import smolyakov.roman.ru.mytask.objects.TodoDocument;

public class DeleteDocumentReceiver extends BroadcastReceiver {
	private ArrayList<TodoDocument> listDocuments;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		listDocuments = ((AppContext) context).getListDocuments();

		int docIndex = intent.getIntExtra(AppContext.DOC_INDEX, -1);

		if (docIndex >= 0) {
			TodoDocument removedDocument = listDocuments.remove(docIndex);
			getCurrentTodoFile(removedDocument).delete();

		} else {

			ArrayList<Integer> indexes = intent.getIntegerArrayListExtra(AppContext.DOC_INDEXES);

			int i = 0;

			for (Integer index : indexes) {

				if (i > 0) {
					index -= i;
				}

				TodoDocument removedDocument = listDocuments.remove(index
						.intValue());

				i++;

				getCurrentTodoFile(removedDocument).delete();

			}
			
			
			Intent intentBroadcast = new Intent(AppContext.RECEIVER_REFRESH_LISTVIEW);
			LocalBroadcastManager.getInstance(context).sendBroadcast(intentBroadcast);
		}
	}

	private File getCurrentTodoFile(TodoDocument todoDocument) {
		String filePath = ((AppContext) context).getPrefsDir() + "/"
				+ todoDocument.getCreateDate().getTime() + ".xml";
		return new File(filePath);
	}

}
