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

package net.twobeone.remotehelper.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import net.twobeone.remotehelper.R;
import net.twobeone.remotehelper.util.TBOUtil;

public class POISearchDialog extends Activity {

	private EditText poi_name;
	private Button search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_poi);

		poi_name = (EditText) findViewById(R.id.edit_poi);
		search = (Button) findViewById(R.id.search);
		search.setBackground(TBOUtil.getDrawable(this, R.drawable.btn_close));
		search.setTextColor(TBOUtil.getColorStateList(this, R.drawable.btn_text));

		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle extra = new Bundle();
				Intent intent = new Intent();
				extra.putString("data", poi_name.getText().toString());
				intent.putExtras(extra);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}