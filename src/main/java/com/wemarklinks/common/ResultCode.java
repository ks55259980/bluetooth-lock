package com.wemarklinks.common;

public class ResultCode {
	/** 成功 */
	public static final int SUCCESS = 200; // "成功"

	/** 没有登录 */
	public static final int NOT_LOGIN = 400; // 没有登录"),

	/** 发生异常 */
	public static final int EXCEPTION = 401; // 发生异常"),

	/** 系统错误 */
	public static final int SYS_ERROR = 402; // 系统错误"),

	/** 参数错误 */
	public static final int PARAMS_ERROR = 403; // 参数错误 "),

	/** 不支持或已经废弃 */
	public static final int NOT_SUPPORTED = 410; // 不支持或已经废弃"),

	/** AuthCode错误 */
	public static final int INVALID_AUTHCODE = 444; // 无效的AuthCode"),

	/** 太频繁的调用 */
	public static final int TOO_FREQUENT = 445; // 太频繁的调用"),

	/** 未知的错误 */
	public static final int UNKNOWN_ERROR = 499; // 未知错误");

	public static final int BALANCE_NOT_ENOUGH = 601;

	/** 操作有误 */
	public static final int WRONG_OPERATION = 602;

	/** 文件过大 */
	public static final int FILE_TOO_LARGE = 604;

	/** 结果为空 */
	public static final int RES_NULL = 700; // 结果为空 ");

	/** code没有绑定活动 */
	public static final int ACT_NULL = 701; // code没有绑定活动 ");

	/** 更新失败 */
	public static final int UPDATE_FAIL = 702; // 更新失败 ");

	/** 接口限制 */
	public static final int API_LIMIT = 703; // 更新失败 ");

	/** 失败 */
	public static final int FAIL = 704; // 失败 ");

}
