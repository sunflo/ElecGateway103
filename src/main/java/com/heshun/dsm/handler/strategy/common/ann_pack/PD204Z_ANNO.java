package com.heshun.dsm.handler.strategy.common.ann_pack;

import com.heshun.dsm.handler.anno.Transform;
import com.heshun.dsm.handler.anno.Transform.DataGroup;
import com.heshun.dsm.handler.strategy.common.CommonElecPack;

public class PD204Z_ANNO extends CommonElecPack {
	public PD204Z_ANNO(int address) {
		super(address);

	}

	@Transform(index = 1, tag = "ua")
	public float _ua;
	@Transform(index = 2, tag = "ub")
	public float _ub;
	@Transform(index = 3, tag = "uc")
	public float _uc;
	@Transform(index = 4, tag = "uab")
	public float _uab;
	@Transform(index = 5, tag = "ubc")
	public float _ubc;
	@Transform(index = 6, tag = "uca")
	public float _uca;
	@Transform(index = 7, tag = "ia")
	public float _ia;
	@Transform(index = 8, tag = "ib")
	public float _ib;
	@Transform(index = 9, tag = "ic")
	public float _ic;
	@Transform(index = 10, tag = "pa", ratio = 1000)
	public float _pa;
	@Transform(index = 11, tag = "pb", ratio = 1000)
	public float _pb;
	@Transform(index = 12, tag = "pc", ratio = 1000)
	public float _pc;
	@Transform(index = 13, tag = "pt", ratio = 1000)
	public float _ptotal;// 总有功功率
	@Transform(index = 14, tag = "qa", ratio = 1000)
	public float _qa;
	@Transform(index = 15, tag = "qb", ratio = 1000)
	public float _qb;
	@Transform(index = 16, tag = "qc", ratio = 1000)
	public float _qc;
	@Transform(index = 17, tag = "qt", ratio = 1000)
	public float _qtotal;// 总无功功率
	@Transform(index = 18, tag = "sa", ratio = 1000)
	public float _sa;
	@Transform(index = 19, tag = "sb", ratio = 1000)
	public float _sb;
	@Transform(index = 20, tag = "sc", ratio = 1000)
	public float _sc;
	@Transform(index = 21, tag = "st", ratio = 1000)
	public float _stotal;// 总视在功率
	@Transform(index = 22, tag = "pfa")
	public float _pfa;
	@Transform(index = 23, tag = "pfb")
	public float _pfb;
	@Transform(index = 24, tag = "pfc")
	public float _pfc;
	@Transform(index = 25, tag = "pft")
	public float _pftotal;
	@Transform(index = 26, tag = "freq")
	public float _freq;
	//
	@Transform(index = 1, group = DataGroup.YM, tag = "epi",ratio = 1000)
	public long epi;// 正向有功电度
	@Transform(index = -1, group = DataGroup.YM, tag = "epe",ratio = 1000)
	public long epe;// 负向有功电度
	@Transform(index = 2, group = DataGroup.YM, tag = "eql",ratio = 1000)
	public long eql;// 正向无功电度
	@Transform(index = -1, group = DataGroup.YM, tag = "eqc",ratio = 1000)
	public long eqc;// 负向无功电度

	@Override
	public String getDeviceType() {

		return "PD204_E";
	}
}
