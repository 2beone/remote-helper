
package net.twobeone.remotehelper.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.twobeone.remotehelper.R;

public class Status_Item_Adapter extends BaseAdapter {
	private ArrayList<Status_Item> listViewItemList = new ArrayList<Status_Item>();

	public Status_Item_Adapter() {

	}

	@Override
	public int getCount() {
		return listViewItemList.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		final Context context = parent.getContext();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.status_item, parent, false);
		}

		TextView titleTextView = (TextView) convertView.findViewById(R.id.item_name);
		TextView descTextView = (TextView) convertView.findViewById(R.id.item_info);

		Status_Item listViewItem = listViewItemList.get(position);

		titleTextView.setText(listViewItem.getTitle());
		descTextView.setText(listViewItem.getDesc());

		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return listViewItemList.get(position);
	}

	public void addItem(String title, String desc) {
		Status_Item item = new Status_Item();

		item.setTitle(title);
		item.setDesc(desc);

		listViewItemList.add(item);
	}

	public void deleteItem() {
		listViewItemList.clear();
	}
}
