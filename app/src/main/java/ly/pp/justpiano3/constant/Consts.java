package ly.pp.justpiano3.constant;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.provider.BaseColumns;
import ly.pp.justpiano3.R;

public interface Consts extends BaseColumns {
    String[] items = new String[]{"  收藏夹 ", "经典乐章", "流行空间", "影视剧场", "儿时回忆", "动漫原声", "游戏主题", "红色歌谣", "原创作品"};
    //    String[] f5601e = new String[]{"极尽琴乐", "琴心联语", "以琴会友", "高山流水", "剑胆琴心"};
    String[] nameCL = new String[]{"", "巧遇钢琴", "黑白之梦", "钢琴学徒", "小有成就", "肖练超技", "名动一时", "顶尖琴手", "梦圆指尖", "名不虚传", "名列神迹", "超神存在", "琴键狂魔"};
    String[] sortNames = new String[]{"名称升序", "名称降序", "最新曲目", "近期弹奏", "难度升序", "难度降序", "得分升序", "得分降序", "时长升序", "时长降序"};
    String[] localMenuListNames = new String[]{"参数设置", "曲库同步", "数据导出", "录音文件"};
    String[] sortSyntax = new String[]{"name asc", "name desc", "isnew desc", "date desc", "diff asc", "diff desc", "score asc", "score desc", "length asc", "length desc"};
    String[] noteSpeed = new String[]{"神快", "神快", "超快", "很快", "快", "中", "中"};
    String[] hand = new String[]{"右手", "左手"};
    String[] groups = new String[]{"蓝队", "黄队", "红队"};
    String[] coupleType = new String[]{"情侣证书", "基友证书", "百合证书"};
    String[] sqlColumns = new String[]{"_id", "name", "item", "path", "diff", "isfavo", "length", "Ldiff"};
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
            R.drawable.b141, R.drawable.b142, R.drawable.b143};
    int[] colors = new int[]{R.color.translent, R.color.c1, R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6, R.color.c7, R.color.c8, R.color.c9, R.color.c10, R.color.c11, R.color.c12};
    int[] couples = new int[]{R.drawable._none, R.drawable.couple_1, R.drawable.couple_2, R.drawable.couple_3};
    int[] sex = new int[]{R.drawable.m, R.drawable.f, R.drawable.none, R.drawable._none};
    int[] filledKuang = new int[]{R.drawable.filled_msg, R.drawable.filled_v1, R.drawable.filled_v2, R.drawable.filled_v3, R.drawable.filled_v4, R.drawable.filled_v5, R.drawable.filled_v6,
            R.drawable.filled_v7, R.drawable.filled_v8, R.drawable.filled_v9, R.drawable.filled_v10, R.drawable.filled_v11, R.drawable.filled_v12,
            R.drawable.filled_v13, R.drawable.filled_v14, R.drawable.filled_v15, R.drawable.filled_v16, R.drawable.filled_v17, R.drawable.filled_v18,
            R.drawable.filled_v19, R.drawable.filled_v20, R.drawable.filled_v21, R.drawable.filled_v22, R.drawable.filled_v23, R.drawable.filled_v24,
            R.drawable.filled_v25, R.drawable.filled_v26, R.drawable.filled_v27};
    int[] groupModeColor = new int[]{R.drawable.back_puased, R.drawable.v1_name, R.drawable.v6_name, R.drawable.v7_name};
    int[] kuang = new int[]{R.drawable.title_bar, R.drawable.v1_name, R.drawable.v2_name, R.drawable.v3_name, R.drawable.v4_name, R.drawable.v5_name, R.drawable.v6_name,
            R.drawable.v7_name, R.drawable.v8_name, R.drawable.v9_name, R.drawable.v10_name, R.drawable.v11_name, R.drawable.v12_name,
            R.drawable.v13_name, R.drawable.v14_name, R.drawable.v15_name, R.drawable.v16_name, R.drawable.v17_name, R.drawable.v18_name,
            R.drawable.v19_name, R.drawable.v20_name, R.drawable.v21_name, R.drawable.v22_name, R.drawable.v23_name, R.drawable.v24_name,
            R.drawable.v25_name, R.drawable.v26_name, R.drawable.v27_name};
    PorterDuffColorFilter[] kuangColorFilterAdd = new PorterDuffColorFilter[]{
            new PorterDuffColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0x7fffcc00, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff6600, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff3333, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff0066, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff00cc, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff00ff, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f330000, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f00cc00, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff66ff, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f0000cc, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f000033, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f000000, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f72ff00, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f66FFFF, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f009999, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f333399, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f9933CC, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f777777, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f9999FF, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fD3A29E, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f888ABA, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f475A95, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f996666, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f999900, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7fff00cc, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f00cc00, PorterDuff.Mode.ADD),
            new PorterDuffColorFilter(0x7f999900, PorterDuff.Mode.ADD)};
    PorterDuffColorFilter[] kuangColorFilterMultiPly = new PorterDuffColorFilter[]{
            new PorterDuffColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffffcc00, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff6600, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff3333, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff0066, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff00cc, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff00ff, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff330000, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff00cc00, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff66ff, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff0000cc, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff000033, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff72ff00, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff66FFFF, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff009999, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff333399, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff9933CC, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff777777, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff9999FF, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffD3A29E, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff888ABA, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff475A95, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff996666, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff999900, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xffff00cc, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff00cc00, PorterDuff.Mode.MULTIPLY),
            new PorterDuffColorFilter(0xff999900, PorterDuff.Mode.MULTIPLY)};
    int[] fHair = new int[]{0, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10, 15, 15, 15, 15, 15, 15, 25,
            25, 20, 20, 25, 25, 15, 25, 25, 25, 20, 20, 20, 25, 25, 25, 25, 25, 25, 20, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 20, 20, 20, 15, 15, 20, 25, 20, 15, 15, 15, 15, 15, 15, 15, 20, 20, 20, 20, 20, 15, 15, 20, 20, 20,
            20, 20, 20, 15, 15, 15, 15, 15, 15, 15, 15,
            25, 25, 25, 25, 25, 20, 10};
    int[] mHair = new int[]{0, 10, 10, 10, 15, 15, 15, 15, 10, 10, 10, 15, 15, 20, 20, 20, 20, 15, 15, 15, 20, 25, 20,
            20, 15, 20, 20, 15, 20, 25, 15, 20, 15, 15, 15, 15, 15, 20, 25, 25, 25, 25, 25, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 20, 20, 20, 20, 20, 20, 20, 25, 15, 15, 15,
            10};
    int[] fEye = new int[]{0, 20, 20, 20, 20, 20, 20, 20, 20, 20, 25, 25, 25, 25, 25, 25, 20,
            20};
    int[] mEye = new int[]{0, 20, 20, 20, 20, 20, 20, 20, 20, 20, 25, 25, 25, 25, 25, 25, 20};
    int[] fJacket = new int[]{0, 10, 10, 15, 15, 15, 15, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 25, 10, 10, 10,
            10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 20, 30, 25, 25, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30,
            30, 30, 30, 50, 55, 55, 50, 55, 55, 50, 55, 50, 50, 50, 50, 15, 25, 25, 25, 25, 25, 25, 30, 30, 30, 20, 20, 45, 20, 20,
            20, 45, 20, 15, 15, 15, 15, 25, 20, 20, 20, 20, 20, 30, 30, 30, 35, 35, 40, 45, 30, 15, 30,
            45, 35, 20};
    int[] mJacket = new int[]{0, 10, 10, 15, 15, 15, 15, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 25, 10,
            10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 20, 10, 20, 30, 55, 55, 55, 55, 50, 55, 75, 15, 35, 20,
            20, 45, 45, 15, 15, 15, 20, 40, 30, 15, 15, 20, 20, 20, 20, 20, 20, 20, 40, 40, 20, 20, 20, 20, 45, 45, 15, 20, 15, 20, 35, 50, 25,
            15, 20};
    int[] fTrousers = new int[]{0, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10, 10, 15, 15, 15, 15, 15, 15, 15,
            15};
    int[] mTrousers = new int[]{0, 10, 10, 10, 10, 10, 15, 15, 15, 25, 10, 10, 15, 15, 15, 10, 10, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 20, 20, 10, 15, 15, 15, 15, 10, 15, 15, 15, 15, 15, 15, 15, 10, 10, 10, 10, 10, 15, 10, 15, 15,
            15};
    int[] fShoes = new int[]{0, 10, 10, 10, 10, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10,
            10, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 10, 10, 10, 10, 5,
            10, 15};
    int[] mShoes = new int[]{0, 15, 15, 15, 15, 15, 15, 15, 10, 10, 10, 10, 10, 10, 10, 15, 10, 10, 10, 10, 10,
            10, 15, 15, 15, 10, 10, 10, 15, 15, 15, 15, 15, 15, 15, 15, 10, 10, 15, 15, 15, 15, 10, 10, 10, 10, 10, 15, 10, 15, 15,
            15};
}
