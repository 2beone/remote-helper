/*
 * Copyright 2014 Pierre Chabardes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.twobeone.remotehelper.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.twobeone.remotehelper.R;

public class MSG_Item_Adapter extends BaseAdapter {
	private ArrayList<MSG_Item> listViewItemList = new ArrayList<MSG_Item>();

	public MSG_Item_Adapter() {

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
			convertView = inflater.inflate(R.layout.msg_name, parent, false);
		}

		ImageView iconImageView = (ImageView) convertView.findViewById(R.id.msg_img);
		TextView titleTextView = (TextView) convertView.findViewById(R.id.name);

		MSG_Item listViewItem = listViewItemList.get(position);

		iconImageView.setImageDrawable(listViewItem.getIcon());
		titleTextView.setText(listViewItem.getTitle());

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

	public void addItem(Drawable icon, String title, String Extend) {
		MSG_Item item = new MSG_Item();

		item.setIcon(icon);
		item.setTitle(title);
		item.setExtend(Extend);

		listViewItemList.add(item);
	}

	public void deleteItem() {
		listViewItemList.clear();
	}
}