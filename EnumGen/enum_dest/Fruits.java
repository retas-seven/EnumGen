package common;

import java.util.HashMap;
import java.util.Map;

/**
 * 果物のEnumクラス<br>
 * 自動生成：2016/02/23
 * @author 
 * @version 1.0
 */
public enum Fruits {

    /** りんご */
    APPLE("1"),

    /** みかん */
    ORANGE("2"),

    /** バナナ */
    BANANA("3");

	/**
	 * コード値を保持するMap
	 */
	private static final Map<String, Fruits> CODE_MAP = initMap(APPLE, ORANGE, BANANA);

	/**
	 * コード値
	 */
	private final String code;

    /**
     * コンストラクタ
     * @param code コード値
     */
	private Fruits(String code) {
		this.code = code;
	}

    /**
     * コード値を保持するMapを初期化する
     * @param elements 果物のEnum
     * @return コード値を保持するMap
     */
	private static Map<String, Fruits> initMap(Fruits... elements) {
		Map<String, Fruits> map = new HashMap<>();
		for (Fruits element : elements) {
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
     * @return 果物のEnum
     */
	public static Fruits toElement(String code) {
		return CODE_MAP.get(code);
	}

    /**
     * りんごであるか判別する<BR>
     * @return りんごの場合は{@code true}、それ以外は{@code false}
     */
    public boolean isApple() {
        return this == APPLE;
    }

    /**
     * みかんであるか判別する<BR>
     * @return みかんの場合は{@code true}、それ以外は{@code false}
     */
    public boolean isOrange() {
        return this == ORANGE;
    }

    /**
     * バナナであるか判別する<BR>
     * @return バナナの場合は{@code true}、それ以外は{@code false}
     */
    public boolean isBanana() {
        return this == BANANA;
    }
}
