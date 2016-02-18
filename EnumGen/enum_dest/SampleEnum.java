package common;

import java.util.HashMap;
import java.util.Map;

/**
 * サンプル列挙型のEnumクラス<br>
 * 作成：2016/02/18
 * @author 
 * @version 1.0
 */
public enum SampleEnum {

    /**
     * サンプル一
     */
    SAMPLE_ONE("1"),

    /**
     * サンプル二
     */
    SAMPLE_TWO("2"),

    /**
     * サンプル三
     */
    SAMPLE_THREE("3");

	/**
	 * コード値を保持するMap
	 */
	private static final Map<String, SampleEnum> CODE_MAP = initMap(SAMPLE_ONE, SAMPLE_TWO, SAMPLE_THREE);

	/**
	 * コード値
	 */
	private final String code;

    /**
     * コンストラクタ
     * @param code コード値
     */
	private SampleEnum(String code) {
		this.code = code;
	}

    /**
     * コード値を保持するMapを初期化する
     * @param elements サンプル列挙型のEnum
     * @return コード値を保持するMap
     */
	private static Map<String, SampleEnum> initMap(SampleEnum... elements) {
		Map<String, SampleEnum> map = new HashMap<>();
		for (SampleEnum element : elements) {
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
     * @return サンプル列挙型のEnum
     */
	public static SampleEnum toElement(String code) {
		return CODE_MAP.get(code);
	}

    /**
     * サンプル一であるか判別する
     * 
     * @return サンプル一の場合は{@code true}、その他は{@code false}
     */
    public boolean isSampleOne() {
        return this == SAMPLE_ONE;
    }

    /**
     * サンプル二であるか判別する
     * 
     * @return サンプル二の場合は{@code true}、その他は{@code false}
     */
    public boolean isSampleTwo() {
        return this == SAMPLE_TWO;
    }

    /**
     * サンプル三であるか判別する
     * 
     * @return サンプル三の場合は{@code true}、その他は{@code false}
     */
    public boolean isSampleThree() {
        return this == SAMPLE_THREE;
    }
}
