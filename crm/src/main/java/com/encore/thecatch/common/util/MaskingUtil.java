package com.encore.thecatch.common.util;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Service
public class MaskingUtil {
    // 이름 마스킹 (이름 가운데)
    public String nameMasking(String name) {
        String regex = "^(.)(.*)$"; // 첫 글자 그룹, 나머지 모든 문자 그룹
        Matcher matcher = Pattern.compile(regex).matcher(name);

        if (matcher.find()) {
            String firstChar = matcher.group(1); // 첫 글자 추출
            String rest = matcher.group(2); // 나머지 문자 추출
            String maskedRest = "*".repeat(rest.length()); // 나머지 문자 마스킹

            return firstChar + maskedRest;
        }

        return name;
    }

    // 휴대폰번호 마스킹(가운데 숫자 4자리 마스킹)
    public String phoneMasking(String phoneNo) throws Exception {
        String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";

        Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
        if(matcher.find()) {
            String target = matcher.group(2);
            int length = target.length();
            char[] c = new char[length];
            Arrays.fill(c, '*');

            return phoneNo.replace(target, String.valueOf(c));
        }
        return phoneNo;
    }

    public String employeeNumberMasking(String employeeNumber) {
        int length = employeeNumber.length();
        if (length >= 5) { // 사원 번호가 5자리 이상이어야 마스킹 가능
            String maskedString = "*****"; // 마스킹할 부분을 ***** 로 대체

            return employeeNumber.substring(0, length - 3) + maskedString;
            // 마지막 세 자리를 제외한 나머지 부분과 마스킹한 부분을 합쳐 반환
        } else {
            return employeeNumber; // 사원 번호가 5자리 미만이면 그냥 반환
        }
    }


    // 이메일 마스킹(앞3자리 이후 '@'전까지 마스킹)
    public String emailMasking(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String prefix = email.substring(0, atIndex);
            String maskedPrefix = prefix.substring(0, Math.min(3, prefix.length())) + "*****";
            return maskedPrefix + email.substring(atIndex);
        } else {
            return email; // 이메일 주소에 "@" 기호가 없는 경우 그냥 반환
        }
    }


    // 생년월일 마스킹(8자리)
    public String birthMasking(String birthday) throws Exception {
        String regex = "^((19|20)\\d\\d)?([-/.])?(0[1-9]|1[012])([-/.])?(0[1-9]|[12][0-9]|3[01])$";

        Matcher matcher = Pattern.compile(regex).matcher(birthday);
        if(matcher.find()) {
            return birthday.replace("[0-9]", "*");
        }
        return birthday;
    }

    // 주소 마스킹(신주소, 구주소, 도로명 주소 숫자만 전부 마스킹)
    public String addressMasking(String address) throws Exception {
        // 신(구)주소, 도로명 주소
        String regex = "(([가-힣]+(\\d{1,5}|\\d{1,5}(,|.)\\d{1,5}|)+(읍|면|동|가|리))(^구|)((\\d{1,5}(~|-)\\d{1,5}|\\d{1,5})(가|리|)|))([ ](산(\\d{1,5}(~|-)\\d{1,5}|\\d{1,5}))|)|";
        String newRegx = "(([가-힣]|(\\d{1,5}(~|-)\\d{1,5})|\\d{1,5})+(로|길))";

        Matcher matcher = Pattern.compile(regex).matcher(address);
        Matcher newMatcher = Pattern.compile(newRegx).matcher(address);
        if(matcher.find()) {
            return address.replaceAll("[0-9]", "*");
        } else if(newMatcher.find()) {
            return address.replaceAll("[0-9]", "*");
        }
        return address;
    }
}
