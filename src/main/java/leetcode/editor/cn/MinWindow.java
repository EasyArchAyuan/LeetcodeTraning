package leetcode.editor.cn;

/**
 * @author Ayuan
 * @Description: 剑指 Offer II 017. 含有所有字符的最短字符串
 * @date 2022/7/20 15:27
 */
public class MinWindow {
    /**
     * 解体思路
     * 1.英文字母的ASCII码只占58位 (A~Z: 65~90, a~z: 97~122)，以一个大小为58的hash[]数组记录t的字符集。遍历t的字符，初始化hash数组，每一个位置代表一个字符，记录相应字符的个数。例如对于t = "AABCC"得到hash = {2, 1, 2, 0, ... ,0} (省略后55位的0)。
     * 2.在s上设置长度为t.length()的窗口，动态地调整hash，即考察该窗口的每一个字符，在hash相应位置上减1（表示窗内字符覆盖了t中的该字符）。第一个窗口对hash的调整可以与hash初始化同时开始。
     * 3.若第一个窗口符合要求，那么将得到一个全0的hash，又因为题目声明答案唯一，因此可直接返回该窗截取的子串。否则应该设置一个diff来跟踪当前窗调整hash后的字符差异。第一个窗调整hash过后，累计hash上大于0的位置个数，有多少位大于0，就说明当前窗尚未覆盖这些字符（及其相应的个数）。diff表示当前窗尚未覆盖到的t的字符的种类数。
     * 4.此后窗口右界逐位向右移动，考察进入的字符（称之为in）。字符in使得当前窗有机会覆盖t中的字符（导致hash[in]--）。若hash[in]-- 后有hash[in] == 0，说明此时窗内in字符刚好覆盖了t中的in字符，不多也不少，于是diff--。紧接着考察是否有diff == 0，若满足，则说明此时的窗覆盖了所有t中的字符，且窗的右界不可更小，为右界合格窗。
     * 5.由于左界还存在缩小的可能，因此尝试在满足diff == 0的条件下连续地收缩左界。离开的字符（称之为out）使得hash在该位字符上加1（hash[out]++）。若hash[out]++ 后hash[out] == 1，说明out的离开将导致当前窗刚好比t少一个out字符，那么从out开始到当前窗右界的范围即为一个不可再收缩的合格窗。
     * 6.若此合格窗小于当前最小合格窗，则更新当前最小合格窗的左右界。
     * 7.此时左界已经收缩到刚好比t少一个out，于是右界向右一位继续考察，直到到达s右边界。
     */
    public static void main(String[] args) {
        MinWindow.Solution solution = new MinWindow().new Solution();
        String out = solution.minWindow("ADOBECODEBANC", "ABC");
        System.out.println(out);
    }

    class Solution {
        public String minWindow(String s, String t) {
            int ns = s.length(), nt = t.length();
            if (ns < nt) {
                return "";
            }
            int[] hash = new int[58]; // A~Z: 65~90, a~z: 97~122
            int diff = 0;
            for (int i = 0; i < nt; i++) {
                hash[t.charAt(i) - 'A']++;
                hash[s.charAt(i) - 'A']--;
            }
            //只关心未抵消的字符
            for (int val : hash) {
                if (val > 0) {
                    diff++;
                }
            }
            //第一个窗为最小覆盖子串时
            if (diff == 0) {
                return s.substring(0, nt);
            }
            int l = 0, r = nt, lmin = 0, rmin = ns;
            //只要当前窗还未覆盖，向右侧扩窗
            for (; r < ns; r++) {
                int in = s.charAt(r) - 'A';
                hash[in]--;
                // in入窗后使得窗内该字符个数与t中相同
                if (hash[in] == 0) {
                    diff--;
                }
                // diff不为0则继续扩窗
                if (diff != 0) {
                    continue;
                }
                // 从左侧缩窗
                for (; diff == 0; l++) {
                    int out = s.charAt(l) - 'A';
                    hash[out]++;
                    if (hash[out] == 1) {
                        diff++;
                    }
                }
                // 缩窗后得到一个合格窗，若窗宽更小，更新窗界
                if (r - l + 2 < rmin - lmin + 1) {
                    lmin = l - 1;
                    rmin = r;
                }
            }
            // 根据窗界是否有过更新来返回相应的结果
            return rmin == ns ? "" : s.substring(lmin, rmin + 1);
        }
    }
}
