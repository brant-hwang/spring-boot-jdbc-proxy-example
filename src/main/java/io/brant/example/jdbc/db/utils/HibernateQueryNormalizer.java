package io.brant.example.jdbc.db.utils;

public class HibernateQueryNormalizer {
	public static final int BLANK = 32;


	// 샘플구현
	public String format(String query) {
		if (query.contains("select") && query.contains("from")) {

			// SELECT 절에 있는 as 제거
			String fields = query.substring(query.indexOf("select") + 6, query.indexOf("from")).replace("\n", "");

			for (String field : fields.split(",")) {
				String as = field.substring(field.indexOf("as ")).trim();
				query = query.replaceAll(as, "").replaceAll(" ,", ",");
			}

			// FROM 절에 있는 alias와 SELECT에 있는 alias. 들을 공백으로 치환.
			String froms = query.substring(query.indexOf("from") + 4, query.indexOf("where")).replace("\n", "");

			StringBuilder filteredFroms = new StringBuilder();

			for (int i = 0; i < froms.length(); i++) {
				if (froms.charAt(i) != BLANK || !isFormattedBlank(froms, i)) {
					filteredFroms.append(froms.charAt(i));
				}
			}

			for (String from : filteredFroms.toString().split(",")) {
				String tableAlias = from.split(" ")[1];
				query = query.replaceAll(tableAlias + ".", "").replaceAll(tableAlias, "");
			}
		}

		return query;
	}

	public boolean isFormattedBlank(String string, int index) {
		try {
			int prevIndex = index - 1;
			int nextIndex = index + 1;

			char prevChar = string.charAt(prevIndex);
			char currentChar = string.charAt(index);
			char nextChar = string.charAt(nextIndex);

			if (prevChar != BLANK && currentChar == BLANK && nextChar != BLANK)
				return false;

		} catch (Exception e) {
			// ignore
		}

		return true;
	}
}
