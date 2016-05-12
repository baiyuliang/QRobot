package com.byl.qrobot.util;

public class StringUtil {
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 全角空格为12288
			if (c[i] == 12288) {
				// 半角空格为32
				c[i] = (char) 32;
				continue;
			}
			// 半角(33-126)与全角(65281-65374)的对应关系是：相差65248
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	public static String CN2EN(String input) {
		String[] regs = {
				"！", "，", "。", "；","？","：","“","”","‘","’","（ ","）","──","······",
				"!", ",", ".", ";" ,"?",":","\"","\"","'","'","( ",")","-","......",
		};
		for (int i = 0; i < regs.length / 2; i++) {
			input = input.replaceAll(regs[i], regs[i + regs.length / 2]);
		}

		return ToDBC(input);
	}
}
