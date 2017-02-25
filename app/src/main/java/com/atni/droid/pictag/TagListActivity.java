package com.atni.droid.pictag;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TagListActivity extends BaseActivity {

    private TagView tagGroup;

    private EditText editText;

    private ArrayList<TagClass> tagList;

    private ArrayList<Tag> tags = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_tagged:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        editText = (EditText) findViewById(R.id.editText);
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                setTags(s);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


        tagGroup = (TagView) findViewById(R.id.tag_group);
        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, int position) {
                Toast.makeText(TagListActivity.this, "Long Click: " + tag.text, Toast.LENGTH_SHORT).show();
            }
        });

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                editText.setText(tag.text);
                editText.setSelection(tag.text.length());//to set cursor position

            }
        });
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TagListActivity.this);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(TagListActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_search_tag);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Search tags", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        prepareTags();

    }

    private void setTags(CharSequence cs) {
        /**
         * for empty edittext
         */
//        if (cs.toString().equals("")) {
//            return;
//        }

        String text = cs.toString();

        ArrayList<Tag> filteredTags = new ArrayList<>();
        for (Tag t:tags) {
            if (t.text.toLowerCase().startsWith(text.toLowerCase())) {
                filteredTags.add(t);
            }
        }

        if(filteredTags.isEmpty()) {
            tagGroup.addTags(tags);
            return;
        } else {
            tagGroup.addTags(filteredTags);
        }
//        tagGroup.addTags(
//                tags.stream().filter(s-> s.text.toLowerCase().startsWith(text.toLowerCase()))
//                        .collect(Collectors.toList()));

    }




    private void prepareTags() {
        tagList = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(Constants.COUNTRIES);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                tagList.add(new TagClass(temp.getString("code"), temp.getString("name")));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Tag tag;

        for (int i = 0; i < tagList.size(); i++) {
                tag = new Tag(tagList.get(i).getName());
                tag.radius = 10f;
                tag.layoutColor = Color.parseColor(tagList.get(i).getColor());
                if (i % 2 == 0) // you can set deletable or not
                    tag.isDeletable = true;
                tags.add(tag);
        }
        tagGroup.addTags(tags);
    }

}
