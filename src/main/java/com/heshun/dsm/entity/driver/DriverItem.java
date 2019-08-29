package com.heshun.dsm.entity.driver;

/**
 * 仪表虚拟驱动的item条目描述类
 */
public class DriverItem {
    /**
     * 最终输出json的key
     */
    private String mTag;
    /**
     * 数据报中对应的index
     */
    private int mIndex;
    /**
     * 数据报中所在的分组
     */
    private l03DataGroup mGroup;
    /**
     * 数据类型
     */
    private DataType mDataType;
    /**
     * 是否高低位翻转
     */
    private boolean isReverse;
    /**
     * 倍率转换比率
     */
    private int mRatio;


    public DriverItem(String configLine) {
        //tag<String>,index[1-],group[7,8,a],type[1-8],reverse<boolean>,ratio<int>
        //ia,7,7,1,0,1
        String[] items = configLine.split(",");
        if (items.length != 6)
            throw new IllegalStateException();
        try {
            this.mTag = items[0];
            this.mIndex = Integer.valueOf(items[1]);
            this.mGroup = l03DataGroup.lookup(Integer.valueOf(items[2]));
            this.mDataType = DataType.lookup(Integer.valueOf(items[3]));
            this.isReverse = items[4].equals("1");
            this.mRatio = Integer.valueOf(items[5]);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    public String getKey() {
        return String.format("%d-%d", mGroup.code, mIndex);
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public l03DataGroup getmGroup() {
        return mGroup;
    }

    public void setmGroup(l03DataGroup mGroup) {
        this.mGroup = mGroup;
    }

    public DataType getmDataType() {
        return mDataType;
    }

    public void setmDataType(DataType mDataType) {
        this.mDataType = mDataType;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public int getmRatio() {
        return mRatio;
    }

    public void setmRatio(int mRatio) {
        this.mRatio = mRatio;
    }

    public enum DataType {
        BYT(0), SHT(1), INT(2), SHT_UNS(3), INT_UNS(4), LNG(5), FLT(6), DBLE(7), STR(8);

        private int code;

        DataType(int i) {
            this.code = i;
        }

        public static DataType lookup(int code) {
            for (DataType item : values())
                if (item.code == code)
                    return item;
            return SHT;
        }
    }

    public enum l03DataGroup {
        YC(7), YX(8), YM(10);
        private int code;

        l03DataGroup(int i) {
            this.code = i;
        }

        public static l03DataGroup lookup(int code) {
            for (l03DataGroup item :
                    values())
                if (item.code == code)
                    return item;
            return YC;
        }
    }
}
