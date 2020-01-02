package com.insightsurfface.videocrawler.business.video;

import com.insightsurfface.videocrawler.utils.StringUtil;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class SeekbarTransformer extends DiscreteSeekBar.NumericTransformer {

    @Override
    public int transform(int value) {
        return 0;
    }

    @Override
    public String transformToString(int value) {
        return StringUtil.second2Hour(value);
    }

    @Override
    public boolean useStringTransform() {
        return true;
    }
}
