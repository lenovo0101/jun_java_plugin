package com.jun.plugin.leetcode.algorithm.no0442;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode-cn.com/problems/find-all-duplicates-in-an-array/
 */
@SuppressWarnings("Duplicates")
public class Solution {

    public List<Integer> findDuplicates(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] != i +1
                && nums[i] != nums[nums[i] - 1]) {
                swap(nums, i, nums[i] - 1);
            }
        }

        // 计算重复值
        List<Integer> results = new ArrayList<Integer>();
        for (int i = 0; i < nums.length; i++) {
            // 符合当前位置，直接跳过
            if (nums[i] == i + 1) {
                continue;
            }
            results.add(nums[i]);
        }

        return results;
    }

    private void swap(int[] nums , int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1}));
    }

}
