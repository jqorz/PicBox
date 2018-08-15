package com.jqorz.picbox.adapter;

import android.support.v7.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jqorz.picbox.R;
import com.jqorz.picbox.model.ImageModel;
import com.jqorz.picbox.utils.ToolUtil;
import com.jqorz.picbox.view.RoundImageView;

/**
 * <pre>
 *     copyright: datedu
 *     author : br2ant3
 *     e-mail : xxx@xx
 *     time   : 2018/07/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ImageAdapter extends BaseQuickAdapter<ImageModel, BaseViewHolder> {
    private int gridSize;

    public ImageAdapter(int layoutResId, int gridColumnSize) {
        super(layoutResId);
        this.gridSize = gridColumnSize;
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageModel item) {
        if (item.isLock()) {
            return;
        }
        Glide.with(mContext)
                .load(item.getPath())
                .into((RoundImageView) helper.getView(R.id.img_screen_shot));
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) helper.itemView.getLayoutParams();
        //如果是最后一排，设置底部边距
        if (item.getNum() / gridSize == (item.getGroupNum() - 1) / gridSize) {
            layoutParams.bottomMargin = ToolUtil.dp2px(helper.itemView.getContext(), R.dimen.dp_2);
        } else {
            layoutParams.bottomMargin = ToolUtil.dp2px(helper.itemView.getContext(), R.dimen.dp_m_2);
        }
        //如果是最后一个，设置宽度为平分
        if (item.getNum() == item.getGroupNum() - 1) {
            if (getRecyclerView() != null) {
                layoutParams.width = getRecyclerView().getWidth() / gridSize;
            }
        }

        helper.itemView.setLayoutParams(layoutParams);

    }
}
