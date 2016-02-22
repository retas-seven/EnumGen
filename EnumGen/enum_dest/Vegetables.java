package common;

import java.util.HashMap;
import java.util.Map;

/**
 * 野菜のEnumクラス<br>
 * 自動生成：2016/02/23
 * @author 
 * @version 1.0
 */
public enum Vegetables {

    /** キャベツ */
    CABBAGE("1"),

    /** レタス */
    LETTUCE("2"),

    /** ニンジン */
    CARROT("3"),

    /** ほうれん草 */
    SPINACH("4");

	/**
	 * コード値を保持するMap
	 */
	private static final Map<String, Vegetables> CODE_MAP = initMap(CABBAGE, LETTUCE, CARROT, SPINACH);

	/**
	 * コード値
	 */
	private final String code;

    /**
     * コンストラクタ
     * @param code コード値
     */
	private Vegetables(String code) {
		this.code = code;
	}

    /**
     * コード値を保持するMapを初期化する
     * @param elements 野菜のEnum
     * @return コード値を保持するMap
     */
	private static Map<String, Vegetables> initMap(Vegetables... elements) {
		Map<String, Vegetables> map = new HashMap<>();
		for (Vegetables element : elements) {
			map.put(element.toString(), element);
		}
		return map;
	}

    /**
     * コード値を取得する
     * @return コード値
     */
    public String toString() {
        return code;
    }

    /**
     * コード値に該当するEnumを取得する
     * @param code コード値
     * @return 野菜のEnum
     */
	public static Vegetables toElement(String code) {
		return CODE_MAP.get(code);
	}

    /**
     * キャベツであるか判別する<BR>
     * @return キャベツの場合は{@code true}、それ以外は{@code false}
     */
    public boolean isCabbage() {
        return this == CABBAGE;
    }

    /**
     * レタスであるか判別する<BR>
     * @return レタスの場合は{@code true}、それ以外は{@code false}
     */
    public boolean isLettuce() {
        return this == LETTUCE;
    }

    /**
     * ニンジンであるか判別する<BR>
     * @return ニンジンの場合は{@code true}、それ以外は{@code false}
     */
    public boolean isCarrot() {
        return this == CARROT;
    }

    /**
     * ほうれん草であるか判別する<BR>
     * @return ほうれん草の場合は{@code true}、それ以外は{@code false}
     */
    public boolean isSpinach() {
        return this == SPINACH;
    }
}
