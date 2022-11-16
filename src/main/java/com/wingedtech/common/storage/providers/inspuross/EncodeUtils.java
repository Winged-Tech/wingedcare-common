package com.wingedtech.common.storage.providers.inspuross;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EncodeUtils {
	private static final String DEFAULT_ENCODING = "UTF-8";

	private static final Pattern ENCODED_CHARACTERS_PATTERN;
	static {
		StringBuilder pattern = new StringBuilder();

		pattern
				.append(Pattern.quote("+"))
				.append("|")
				.append(Pattern.quote("*"))
				.append("|")
				.append(Pattern.quote("%7E"))
				.append("|")
				.append(Pattern.quote("%2F"));

		ENCODED_CHARACTERS_PATTERN = Pattern.compile(pattern.toString());
	}


	public static String urlEncode(final String value, final boolean path) {
		if (value == null) {
			return "";
		}

		try {
			String encoded = URLEncoder.encode(value, "UTF-8");

			Matcher matcher = ENCODED_CHARACTERS_PATTERN.matcher(encoded);
			StringBuffer buffer = new StringBuffer(encoded.length());

			while (matcher.find()) {
				String replacement = matcher.group(0);

				if ("+".equals(replacement)) {
					replacement = "%20";
				} else if ("*".equals(replacement)) {
					replacement = "%2A";
				} else if ("%7E".equals(replacement)) {
					replacement = "~";
				} else if (path && "%2F".equals(replacement)) {
					replacement = "/";
				}

				matcher.appendReplacement(buffer, replacement);
			}

			matcher.appendTail(buffer);
			return buffer.toString();

		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		}
	}
}
