package smolyakov.roman.ru.mytask.adapters;


import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import smolyakov.roman.ru.mytask.R;
import smolyakov.roman.ru.mytask.objects.TodoDocument;

public class TodoAdapter extends ArrayAdapter<TodoDocument> {

    private List<TodoDocument> list;
    private View.OnClickListener checkListener;

    public TodoAdapter(Context context, List<TodoDocument> list, View.OnClickListener checkListener) {
        super(context, R.layout.custom_listview_row, R.id.todo_name, list);
        this.checkListener = checkListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_listview_row, parent, false);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.TodoName = (TextView) convertView.findViewById(R.id.todo_name);
            viewHolder.TodoDate = (TextView) convertView.findViewById(R.id.todo_date);
            viewHolder.ImagePriority = (ImageView) convertView.findViewById(R.id.imageTask);
            viewHolder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
//            viewHolder.checkBox.setOnClickListener(checkListener);
            viewHolder.checkBox.setOnClickListener(checkListener);

        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        TodoDocument todoDocument = getItem(position);
        holder.TodoName.setText(todoDocument.getName());
        holder.TodoDate.setText(DateFormat.format("dd MMMM, yyyy, hh:mm", todoDocument.getCreateDate()));
        switch (todoDocument.getPriorityType()) {
            case HIGH:
                holder.ImagePriority.setImageResource(R.drawable.ic_priority_red);
                break;
            case MIDDLE:
                holder.ImagePriority.setImageResource(R.drawable.ic_priority_yellow);
                break;
            case LOW:
                holder.ImagePriority.setImageResource(R.drawable.ic_priority_green);
                break;
            default:
                break;
        }
        todoDocument.setChecked(false);
        holder.checkBox.setTag(todoDocument);
        return convertView;
    }

    static class ViewHolder {
        public TextView TodoName;
        public TextView TodoDate;
        public ImageView ImagePriority;
        public CheckBox checkBox;
    }
}





































