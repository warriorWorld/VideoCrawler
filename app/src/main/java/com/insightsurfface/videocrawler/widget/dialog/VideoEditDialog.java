package com.insightsurfface.videocrawler.widget.dialog;

import android.content.Context;

import com.insightsurfface.stylelibrary.dialog.EditDialog;


public class VideoEditDialog extends EditDialog {
    public VideoEditDialog(Context context) {
        super(context);
    }
    public void setEditText(String text){
        editTextV.setText(text);
        editTextV.setSelection(0,text.length());
    }
}
