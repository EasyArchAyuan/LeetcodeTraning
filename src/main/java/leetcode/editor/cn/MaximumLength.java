package leetcode.editor.cn;

import java.util.*;

/**
 * 2982. 找出出现至少三次的最长特殊子字符串 II
 */
public class MaximumLength {
    public class Solution {
        public int maximumLength(String S) {
            char[] s = S.toCharArray();
            List<Integer>[] groups = new ArrayList[26];
            Arrays.setAll(groups, i -> new ArrayList<>());
            int cnt = 0;
            for (int i = 0; i < s.length; i++) {
                cnt++;
                if (i + 1 == s.length || s[i] != s[i + 1]) {
                    groups[s[i] - 'a'].add(cnt); // 统计连续字符长度
                    cnt = 0;
                }
            }

            int ans = 0;
            for (List<Integer> a : groups) {
                if (a.isEmpty()) continue;
                a.sort(Collections.reverseOrder());
                a.add(0);
                a.add(0); // 假设还有两个空串
                ans = Math.max(ans, Math.max(a.get(0) - 2, Math.max(Math.min(a.get(0) - 1, a.get(1)), a.get(2))));
            }

            return ans > 0 ? ans : -1;
        }
    }
}
