package android.lifeistech.com.dictionary;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarContextView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter;

    EditText wordEditText;
    EditText meanEditText;
    EditText searchWordEditText;

    HashMap<String, String> hashMap;
    TreeSet<String> wordSet;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);

        wordEditText = findViewById(R.id.word);
        meanEditText = findViewById(R.id.mean);
        searchWordEditText = findViewById(R.id.searchWord);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        this.hashMap = new HashMap<>();
        wordSet = new TreeSet<String>();
        pref = getSharedPreferences("dictionary", MODE_PRIVATE);
        editor = pref.edit();

        wordSet.addAll(pref.getStringSet("wordSet", wordSet));

        for (String word : wordSet) {
            this.hashMap.put(word, pref.getString(word, null));
            adapter.add("【" + word + "】" + pref.getString(word, null));
        }

        listView.setAdapter(adapter);

        registerForContextMenu(listView);

        //editor.remove("wordSet");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        int viewId = view.getId();

        if (viewId == R.id.listView) {
            menu.setHeaderTitle("項目メニュー");

            //menu.add(0, 0, 0, "編集");
            menu.add(0, 0, 0, "削除");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int position = info.position;

        switch (item.getItemId()) {
            case 0:
                String i = (String) adapter.getItem(position);
                adapter.remove(i);


                int startIndex = i.indexOf("【") + 1;
                int endIndex = i.indexOf("】");

                i = i.substring(startIndex, endIndex);

                Log.d("Debug", "Key:" + i);

                try {
                    wordSet.remove(i);
                    Log.i("Debug", "wordSet.remove(i)は正常に実行されました。");
                } catch (ClassCastException c) {
                    Log.e("Error", "wordSet.remove(i)で例外が発生しました。(ClassCastException)");
                } catch (NullPointerException n) {
                    Log.e("Error", "wordSet.remove(i)で例外が発生しました。(NullPointerException)");
                }

                editor.remove(i);
                editor.putStringSet("wordSet", wordSet);
                editor.commit();
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset) {
            editor.clear();
            wordSet.clear();
            hashMap.clear();
            adapter.clear();
            Toast.makeText(this, "リセットしました", Toast.LENGTH_SHORT).show();
            Log.d("Debug", "リセットしました");
        }
        return true;
    }

    public void add(View v) {
        String entryWord = wordEditText.getText().toString(); //SharedPreferenceでのkey
        String entryMean = meanEditText.getText().toString(); //SharedPreferenceでのvalue
        String entryObject = "【" + entryWord + "】" + entryMean;

        wordSet.add(entryWord);
        editor.putString(entryWord, entryMean);
        editor.putStringSet("wordSet", wordSet);
        editor.commit();

        adapter.add(entryObject);
    }

    public void search(View v) {
        String searchWord = searchWordEditText.getText().toString();

        wordSet.addAll(pref.getStringSet("wordSet", wordSet));

        for (String word : wordSet) {
            hashMap.put(word, pref.getString(word, null));
        }

        if (hashMap.containsKey(searchWord)) {
            Toast.makeText(this, hashMap.get(searchWord), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "その単語は登録されていません", Toast.LENGTH_LONG).show();
        }
    }
}
