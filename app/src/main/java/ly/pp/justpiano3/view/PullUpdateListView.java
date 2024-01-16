package ly.pp.justpiano3.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

public class PullUpdateListView extends ListView implements AbsListView.OnScrollListener {

    private int totalItemCounts;

    /**
     * 上拉
     */
    private int lassVisible;

    /**
     * 接口回调
     */
    private LoadListener loadListener;

    /**
     * 加载状态
     */
    private boolean isLoading;

    public PullUpdateListView(Context context) {
        super(context);
        this.setOnScrollListener(this);
    }

    public PullUpdateListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnScrollListener(this);
    }

    public PullUpdateListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalItemCounts == lassVisible && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                loadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.lassVisible = firstVisibleItem + visibleItemCount;
        this.totalItemCounts = totalItemCount;
    }

    /**
     * 加载完成
     */
    public void loadComplete() {
        isLoading = false;
    }

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    /**
     * 接口回调
     */
    public interface LoadListener {
        void onLoad();
    }
}
