package com.oodj_assignment;

import com.oodj_assignment.Member.MemberField;
import com.oodj_assignment.UI.menu.GuestMenu;
import com.oodj_assignment.helper.IdGenerator;
import com.oodj_assignment.helper.RecordWriter;

public class Guest extends Customer {

    @Override
    public void viewMenu() {
        new GuestMenu().setVisible(true);
    }
    
    public Member signUp(String fullName, String email, String phoneNum, String password, 
            String confirmPassword) throws Exception {
        Member newMember = new Member();
        newMember.validate(CustomerField.FULLNAME, fullName);
        newMember.validate(MemberField.EMAIL, email);
        newMember.validate(MemberField.PHONENUM, phoneNum);
        newMember.validate(UserField.PASSWORD, password);
        if (null != userID) {
            throw new Exception("This user has been signed up");
        } else if (confirmPassword.trim().isEmpty()) { 
            throw new Exception("Please confirm your password.");
        } else if (!confirmPassword.trim().equals(password.trim())) {
            throw new Exception("Password do not match.");
        }
        String userID = IdGenerator.generate("ctm-");
        RecordWriter.write(new String[] {userID, email.trim(), fullName.trim(), phoneNum.trim(), 
            password.trim()}, "user.txt");
        return new Member(userID);
    }
   
}
