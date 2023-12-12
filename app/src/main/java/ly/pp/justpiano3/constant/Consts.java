package ly.pp.justpiano3.constant;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.database.entity.Song;

public interface Consts {

    String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";

    String[] items = new String[]{"  收藏夹 ", "经典乐章", "流行空间", "影视剧场", "儿时回忆", "动漫原声", "游戏主题", "红色歌谣", "原创作品", "本地导入"};
    //    String[] f5601e = new String[]{"极尽琴乐", "琴心联语", "以琴会友", "高山流水", "剑胆琴心"};
    String[] nameCL = new String[]{"", "巧遇钢琴", "黑白之梦", "钢琴学徒", "小有成就", "肖练超技", "名动一时", "顶尖琴手", "梦圆指尖", "名不虚传", "名列神迹", "超神存在", "琴键狂魔", "开挂人生"};
    // 哦呀 ~ 像苹果这样的废柴 ~ 就算怎样努力也是考不到CL.13的吧？杂鱼 ~ 杂鱼 ~ ❤️
    // 哈 ~ 和挂比们玩耍很恶心的啦 ~ 才不要，嘻嘻 ❤️
    String[] sortNames = new String[]{"名称升序", "名称降序", "最新曲目", "近期弹奏", "难度升序", "难度降序", "得分升序", "得分降序", "时长升序", "时长降序"};
    String[] localMenuListNames = new String[]{"参数设置", "曲库同步", "midi导入", "录音文件", "数据导出"};
    String[] hand = new String[]{"右手", "左手"};
    String[] groups = new String[]{"蓝队", "黄队", "红队"};
    String[] coupleType = new String[]{"情侣证书", "基友证书", "百合证书"};
    int[] helpPic = new int[]{R.drawable.help0, R.drawable.help1, R.drawable.help2, R.drawable.help3};
    Integer[] expressions = new Integer[]{R.drawable.b0, R.drawable.b1, R.drawable.b2, R.drawable.b3, R.drawable.b4, R.drawable.b5, R.drawable.b6, R.drawable.b7, R.drawable.b8, R.drawable.b9, R.drawable.b10,
            R.drawable.b11, R.drawable.b12, R.drawable.b13, R.drawable.b14, R.drawable.b15, R.drawable.b16, R.drawable.b17, R.drawable.b18, R.drawable.b19, R.drawable.b20,
            R.drawable.b21, R.drawable.b22, R.drawable.b23, R.drawable.b24, R.drawable.b25, R.drawable.b26, R.drawable.b27, R.drawable.b28, R.drawable.b29, R.drawable.b30,
            R.drawable.b31, R.drawable.b32, R.drawable.b33, R.drawable.b34, R.drawable.b35, R.drawable.b36, R.drawable.b37, R.drawable.b38, R.drawable.b39, R.drawable.b40,
            R.drawable.b41, R.drawable.b42, R.drawable.b43, R.drawable.b44, R.drawable.b45, R.drawable.b46, R.drawable.b47, R.drawable.b48, R.drawable.b49, R.drawable.b50,
            R.drawable.b51, R.drawable.b52, R.drawable.b53, R.drawable.b54, R.drawable.b55, R.drawable.b56, R.drawable.b57, R.drawable.b58, R.drawable.b59, R.drawable.b60,
            R.drawable.b61, R.drawable.b62, R.drawable.b63, R.drawable.b64, R.drawable.b65, R.drawable.b66, R.drawable.b67, R.drawable.b68, R.drawable.b69, R.drawable.b70,
            R.drawable.b71, R.drawable.b72, R.drawable.b73, R.drawable.b74, R.drawable.b75, R.drawable.b76, R.drawable.b77, R.drawable.b78, R.drawable.b79, R.drawable.b80,
            R.drawable.b81, R.drawable.b82, R.drawable.b83, R.drawable.b84, R.drawable.b85, R.drawable.b86, R.drawable.b87, R.drawable.b88, R.drawable.b89, R.drawable.b90,
            R.drawable.b91, R.drawable.b92, R.drawable.b93, R.drawable.b94, R.drawable.b95, R.drawable.b96, R.drawable.b97, R.drawable.b98, R.drawable.b99, R.drawable.b100,
            R.drawable.b101, R.drawable.b102, R.drawable.b103, R.drawable.b104, R.drawable.b105, R.drawable.b106, R.drawable.b107, R.drawable.b108, R.drawable.b109, R.drawable.b110,
            R.drawable.b111, R.drawable.b112, R.drawable.b113, R.drawable.b114, R.drawable.b115, R.drawable.b116, R.drawable.b117, R.drawable.b118, R.drawable.b119, R.drawable.b120,
            R.drawable.b121, R.drawable.b122, R.drawable.b123, R.drawable.b124, R.drawable.b125, R.drawable.b126, R.drawable.b127, R.drawable.b128, R.drawable.b129, R.drawable.b130,
            R.drawable.b131, R.drawable.b132, R.drawable.b133, R.drawable.b134, R.drawable.b135, R.drawable.b136, R.drawable.b137, R.drawable.b138, R.drawable.b139, R.drawable.b140,
            R.drawable.b141, R.drawable.b142, R.drawable.b143, R.drawable.b144, R.drawable.b145, R.drawable.b146, R.drawable.b147, R.drawable.b148, R.drawable.b149, R.drawable.b150,
            R.drawable.b151
    };
    int[] colors = new int[]{R.color.translent, R.color.c1, R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6, R.color.c7, R.color.c8, R.color.c9, R.color.c10, R.color.c11, R.color.c12, R.color.c13};
    int[] couples = new int[]{R.drawable._none, R.drawable.couple_1, R.drawable.couple_2, R.drawable.couple_3};
    int[] sex = new int[]{R.drawable.m, R.drawable.f, R.drawable.none, R.drawable._none};
    int[] groupModeColor = new int[]{R.drawable.back_puased, R.drawable.v1_name, R.drawable.v6_name, R.drawable.v7_name};

    DiffUtil.ItemCallback<Song> SONG_DIFF_UTIL = new DiffUtil.ItemCallback<Song>() {
        @Override
        public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.equals(newItem);
        }
    };
}
