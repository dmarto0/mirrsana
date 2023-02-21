package org.kymjs.kjframe.demo.widget;import java.util.ArrayList;import org.kymjs.kjframe.KJActivity;import org.kymjs.kjframe.KJBitmap;import org.kymjs.kjframe.bitmap.BitmapConfig;import org.kymjs.kjframe.demo.R;import org.kymjs.kjframe.demo.bean.ImageData;import org.kymjs.kjframe.ui.BindView;import org.kymjs.kjframe.widget.KJListView;import org.kymjs.kjframe.widget.KJRefreshListener;import org.kymjs.kjframe.widget.RoundImageView;import android.view.View;import android.view.ViewGroup;import android.widget.BaseAdapter;import android.widget.TextView;public class KJListViewDemo extends KJActivity {    @BindView(id = R.id.listview)    private KJListView mList;    private KJBitmap kjb;    private ArrayList<DataBean> datas;    @Override    public void setRootView() {        setContentView(R.layout.aty_kjlistview);    }    @Override    public void initData() {        super.initData();        datas = new ArrayList<DataBean>();        BitmapConfig config = new BitmapConfig();        config.isDEBUG = false;        kjb = KJBitmap.create(config);        refresh();    }    private void refresh() {        // 此处添加刷新方法    }    @Override    public void initWidget() {        super.initWidget();        mList.setAdapter(new DemoAdapter());        mList.setOnRefreshListener(new KJRefreshListener() {            @Override            public void onRefresh() {                /** 做耗时操作 */                refresh();                mList.stopRefreshData();            }            @Override            public void onLoadMore() {                /** 做耗时操作 */                refresh();                mList.stopRefreshData();            }        });    }    class DataBean {        String name;        String url;    }    static class ViewHolder {        RoundImageView img;        TextView tv;    }    class DemoAdapter extends BaseAdapter {        @Override        public int getCount() {            return ImageData.imgs.length;        }        @Override        public Object getItem(int position) {            return null;        }        @Override        public long getItemId(int position) {            return 0;        }        @Override        public View getView(int position, View convertView, ViewGroup parent) {            ViewHolder holder = null;            if (convertView == null) {                holder = new ViewHolder();                convertView = View.inflate(aty, R.layout.list_item, null);                holder.img = (RoundImageView) convertView                        .findViewById(R.id.list_item_img);                holder.tv = (TextView) convertView                        .findViewById(R.id.list_item_tv);                convertView.setTag(holder);            } else {                holder = (ViewHolder) convertView.getTag();            }            holder.img.setImageResource(R.drawable.ic_launcher);            return convertView;        }    }}