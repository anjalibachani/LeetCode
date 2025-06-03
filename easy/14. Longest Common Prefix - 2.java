class Solution {
    public String longestCommonPrefix(String[] strs) {
        String common_string1 = strs[0];
        String common_string2 = strs[strs.length-1];

        int index = 0;

        while(index < common_string1.length() && index < common_string2.length()){
            if(common_string1.charAt(index) == common_string2.charAt(index)){
                index++;
            }
            else{
                break;
            }
        }
        return common_string1.substring(0, index);
    }
}