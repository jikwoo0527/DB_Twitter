//repost 기능 아직 미완성 (집계까지만 구현)


package twitter0_wip;

import java.sql.*;
import java.util.Scanner;

public class Twitter {

    // user's info (used mostly)
    static String userId=null;
    static String userPw=null;


    //core components
    static Scanner kb=new Scanner(System.in);
    static Connection conn=null;
    static boolean login=false;



    //Sign up/Log in/change Password
    public static void accessSLC() throws Exception{

        //setting
        Statement stmt=null;
        ResultSet rs=null;
        PreparedStatement pstm=null;
        String sqlStat=null;
        String op=null;


        //Sign up/Log in/change Password

        try {

            while(!login) {

                System.out.println("> Select task: 1.Sign up | 2.Log in | 3.Change password");
                op=kb.nextLine();

                switch(op) {
                    case "1"->{
                        System.out.println("---Sign up---");
                        String newId=null;
                        String newPw=null;
                        String newEmail=null;
                        String newName=null;
                        String newPhoneNum=null;

                        //prepare sql
                        stmt=conn.createStatement();
                        sqlStat="INSERT INTO User(User_ID , Password, Email , Phone_number, Name) VALUES(?,?,?,?,?)";
                        pstm=conn.prepareStatement(sqlStat);

                        //get info
                        System.out.print("> Enter ID: ");
                        newId=kb.nextLine();
                        System.out.print("> Enter Password: ");
                        newPw=kb.nextLine();
                        System.out.print("> Enter Email: ");
                        newEmail=kb.nextLine();
                        System.out.print("> Enter Phone number: ");
                        newPhoneNum=kb.nextLine();
                        System.out.print("> Enter Name: ");
                        newName=kb.nextLine();

                        //setting prepared sql
                        pstm.setString(1,newId);
                        pstm.setString(2,newPw);
                        pstm.setString(3,newEmail);
                        pstm.setString(4,newPhoneNum);
                        pstm.setString(5,newName);

                        //execute DML~
                        pstm.executeUpdate();

                        System.out.println("---Sign up Success!---");

                    }
                    case "2"->{
                        System.out.println("---Log in---");
                        String id=null;
                        String pw=null;

                        System.out.print("> Enter ID: ");
                        id=kb.nextLine();
                        System.out.print("> Enter Password: ");
                        pw=kb.nextLine();

                        //prepare sql
                        stmt=conn.createStatement();
                        sqlStat="SELECT password FROM User WHERE User_ID=\""+id+"\"";
                        rs=stmt.executeQuery(sqlStat);

                        //match check
                        if(rs.next()) {
                            if(pw.equals(rs.getString(1))) {
                                System.out.println("---Log in Success!---");
                                //now we can access various tasks
                                login=true;
                                userId=id;
                                userPw=pw;
                            }
                            else System.out.println("*ERROR!: This Password is wrong");

                        }
                        else System.out.println("*ERROR!: This ID does not exist");


                    }
                    case "3"->{
                        System.out.println("---Change password---");
                        String id=null;
                        String existPw=null;
                        String newPw=null;

                        System.out.print("> Enter ID: ");
                        id=kb.nextLine();
                        System.out.print("> Enter Existing Password: ");
                        existPw=kb.nextLine();

                        //prepare sql
                        stmt=conn.createStatement();
                        sqlStat="SELECT password FROM User WHERE User_ID=\""+id+"\"";
                        rs=stmt.executeQuery(sqlStat);

                        //match check first, if match, change is available
                        if(rs.next()) {
                            if(existPw.equals(rs.getString(1))) {
                                System.out.println("> Enter New Password(max 15): ");
                                newPw=kb.nextLine();

                                stmt=conn.createStatement();
                                sqlStat="UPDATE User SET Password=\""+newPw+"\" WHERE User_ID=\""+id+"\"";
                                stmt.executeUpdate(sqlStat);
                                System.out.println("---Change password Success!---");
                            }
                            else System.out.println("*ERROR!: Existing Password is wrong");

                        }
                        else System.out.println("*ERROR!: This ID does not exist");

                    }
                }

            }

        } catch(SQLException e) {
            e.printStackTrace();
        }


        //if login==true
        showTexts();


    }


    //show timeline, boards
    public static void showTexts() throws Exception{


        //resetting
        Statement stmt=null;
        ResultSet rs=null;
        PreparedStatement pstm=null;
        String sqlStat=null;
        String op=null;
        //login->show->post,like,, etc


        try {
            while(login) {
                //timeline=my text and following users' text; board=my text only  + latest order
                System.out.println("> Select task: 0.Log out | 1.Show timeline | 2.Show my board | 3.Show other user's board | 4.Show other tasks");
                op=kb.nextLine();

                switch(op) {
                    case "0"->{
                        System.out.println("---Log out---");
                        login=false;
                        System.out.println("---Log out Success!---");
                    }
                    case "1"->{
                        System.out.println("---Show timeline---");
                        stmt=conn.createStatement();
                        sqlStat="SELECT * FROM (\r\n"
                                + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts WHERE Writer_ID=\""+userId+"\") \r\n"
                                + "UNION \r\n"
                                + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments WHERE commenter_ID=\""+userId+"\")\r\n"
                                + "UNION\r\n"
                                + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts INNER JOIN Follow ON Posts.Writer_ID=Follow.Followed_ID WHERE Following_ID=\""+userId+"\") \r\n"
                                + "UNION\r\n"
                                + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments INNER JOIN Follow ON Comments.Commenter_ID=Follow.Followed_ID WHERE Following_ID=\""+userId+"\") \r\n"
                                + ")\r\n"
                                + "AS mfpc\r\n"
                                + "ORDER BY mfpc.DATE_time DESC";
                        rs=stmt.executeQuery(sqlStat);
                        //display text
                        String Text=null;
                        String Writer_ID=null;
                        String Date_time=null;
                        String Tid=null;
                        String TextType=null;
                        int Like=0;
                        int Repost=0;
                        while(rs.next()) {
                            Text=rs.getString(1);
                            Writer_ID=rs.getString(2);
                            Date_time=rs.getString(3);
                            Tid=rs.getString(4);
                            TextType=rs.getString(5);

                            //aggregate like
                            stmt=conn.createStatement();
                            //if Post?
                            if(TextType.equalsIgnoreCase("P")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Post_like WHERE P_ID=\""+Tid+"\"";
                                ResultSet rs2=stmt.executeQuery(sqlStat);
                                if(rs2.next()) Like=rs2.getInt(1);

                            }
                            //if Comment?
                            if(TextType.equalsIgnoreCase("C")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID=\""+Tid+"\"";
                                ResultSet rs2=stmt.executeQuery(sqlStat);
                                if(rs2.next()) Like=rs2.getInt(1);

                            }


                            //aggregate repost
                            stmt=conn.createStatement();
                            //if Post?
                            if(TextType.equalsIgnoreCase("P")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID=\""+Tid+"\"";
                                ResultSet rs3=stmt.executeQuery(sqlStat);
                                if(rs3.next()) Repost=rs3.getInt(1);

                            }
                            //if Comment?
                            if(TextType.equalsIgnoreCase("C")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID=\""+Tid+"\"";
                                ResultSet rs3=stmt.executeQuery(sqlStat);
                                if(rs3.next()) Repost=rs3.getInt(1);

                            }


                            System.out.println("|---------------------------------------------");
                            System.out.println("|Writer: "+Writer_ID+" \t Datetime: "+Date_time+" \t TID: "+Tid);
                            System.out.println("|"+Text);
                            System.out.println("|Like: "+ Like+" \t Repost: "+ Repost);
                            System.out.println("|---------------------------------------------");


                        }

                    }
                    case "2"->{
                        System.out.println("---Show my board---");
                        stmt=conn.createStatement();
                        sqlStat="SELECT * FROM (\r\n"
                                + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts WHERE Writer_ID=\""+userId+"\") \r\n"
                                + "UNION \r\n"
                                + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments WHERE Commenter_ID=\""+userId+"\")\r\n"
                                + ") AS mpc\r\n"
                                + "ORDER BY mpc.DATE_time DESC";

                        rs=stmt.executeQuery(sqlStat);
                        //display text
                        String Text=null;
                        String Writer_ID=null;
                        String Date_time=null;
                        String Tid=null;
                        String TextType=null;
                        int Like=0;
                        int Repost=0;
                        while(rs.next()) {
                            Text=rs.getString(1);
                            Writer_ID=rs.getString(2);
                            Date_time=rs.getString(3);
                            Tid=rs.getString(4);
                            TextType=rs.getString(5);

                            //aggregate like
                            stmt=conn.createStatement();
                            //if Post?
                            if(TextType.equalsIgnoreCase("P")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Post_like WHERE P_ID=\""+Tid+"\"";
                                ResultSet rs2=stmt.executeQuery(sqlStat);
                                if(rs2.next()) Like=rs2.getInt(1);

                            }
                            //if Comment?
                            if(TextType.equalsIgnoreCase("C")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID=\""+Tid+"\"";
                                ResultSet rs2=stmt.executeQuery(sqlStat);
                                if(rs2.next()) Like=rs2.getInt(1);

                            }


                            //aggregate repost
                            stmt=conn.createStatement();
                            //if Post?
                            if(TextType.equalsIgnoreCase("P")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID=\""+Tid+"\"";
                                ResultSet rs3=stmt.executeQuery(sqlStat);
                                if(rs3.next()) Repost=rs3.getInt(1);

                            }
                            //if Comment?
                            if(TextType.equalsIgnoreCase("C")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID=\""+Tid+"\"";
                                ResultSet rs3=stmt.executeQuery(sqlStat);
                                if(rs3.next()) Repost=rs3.getInt(1);

                            }


                            System.out.println("|---------------------------------------------");
                            System.out.println("|Writer: "+Writer_ID+" \t Datetime: "+Date_time+" \t TID: "+Tid);
                            System.out.println("|"+Text);
                            System.out.println("|Like: "+ Like+" \t Repost: "+ Repost);
                            System.out.println("|---------------------------------------------");


                        }


                    }
                    case "3"->{
                        String wantId;
                        System.out.println("---Show other user's board---");
                        System.out.print("> Enter User ID you want to see: ");
                        wantId=kb.nextLine();
                        //check is it existing user?

                        //prepare sql
                        stmt=conn.createStatement();
                        sqlStat="SELECT User_ID FROM User WHERE User_ID=\""+wantId+"\"";
                        rs=stmt.executeQuery(sqlStat);

                        //match check
                        if(rs.next()) {
                            //show that user's board
                            stmt=conn.createStatement();
                            sqlStat="SELECT * FROM (\r\n"
                                    + "(SELECT Text, Writer_ID,Date_time,P_ID,\"P\" FROM Posts WHERE Writer_ID=\""+wantId+"\") \r\n"
                                    + "UNION \r\n"
                                    + "(SELECT Text, Commenter_ID,Date_time,C_ID,\"C\" FROM Comments WHERE Commenter_ID=\""+wantId+"\")\r\n"
                                    + ") AS mpc\r\n"
                                    + "ORDER BY mpc.DATE_time DESC";

                            rs=stmt.executeQuery(sqlStat);
                            //display text
                            String Text=null;
                            String Writer_ID=null;
                            String Date_time=null;
                            String Tid=null;
                            String TextType=null;
                            int Like=0;
                            int Repost=0;
                            while(rs.next()) {
                                Text=rs.getString(1);
                                Writer_ID=rs.getString(2);
                                Date_time=rs.getString(3);
                                Tid=rs.getString(4);
                                TextType=rs.getString(5);

                                //aggregate like
                                stmt=conn.createStatement();
                                //if Post?
                                if(TextType.equalsIgnoreCase("P")) {

                                    sqlStat="SELECT COUNT(User_ID) FROM Post_like WHERE P_ID=\""+Tid+"\"";
                                    ResultSet rs2=stmt.executeQuery(sqlStat);
                                    if(rs2.next()) Like=rs2.getInt(1);

                                }
                                //if Comment?
                                if(TextType.equalsIgnoreCase("C")) {

                                    sqlStat="SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID=\""+Tid+"\"";
                                    ResultSet rs2=stmt.executeQuery(sqlStat);
                                    if(rs2.next()) Like=rs2.getInt(1);

                                }


                                //aggregate repost
                                stmt=conn.createStatement();
                                //if Post?
                                if(TextType.equalsIgnoreCase("P")) {

                                    sqlStat="SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID=\""+Tid+"\"";
                                    ResultSet rs3=stmt.executeQuery(sqlStat);
                                    if(rs3.next()) Repost=rs3.getInt(1);

                                }
                                //if Comment?
                                if(TextType.equalsIgnoreCase("C")) {

                                    sqlStat="SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID=\""+Tid+"\"";
                                    ResultSet rs3=stmt.executeQuery(sqlStat);
                                    if(rs3.next()) Repost=rs3.getInt(1);

                                }

                                System.out.println("|---------------------------------------------");
                                System.out.println("|Writer: "+Writer_ID+" \t Datetime: "+Date_time+" \t TID: "+Tid);
                                System.out.println("|"+Text);
                                System.out.println("|Like: "+ Like+" \t Repost: "+ Repost);
                                System.out.println("|---------------------------------------------");


                            }

                        }
                        else System.out.println("*ERROR!: This ID does not exist");
                    }
                    case "4"->{
                        coreTask();

                    }

                }


            }
        }catch(SQLException e1) {
            e1.printStackTrace();
        }



        // if login==false, to main login window
        accessSLC();
    }


    //other core tasks; write post, like post...etc
    public static void coreTask() throws Exception{


        //other core tasks

        //resetting
        Statement stmt=null;
        ResultSet rs=null;
        PreparedStatement pstm=null;
        String sqlStat=null;
        String op=null;


        try {

            //write post-> dont need to writer id again  comment->need to parent post's writer id
            System.out.println("> Select task: 1.Write Post&Comment | 2.Like Post&Comment | 3.Follow/Unfollow | 4.Show Follower/Following list | 5.Search Post&Comment | 6.Repost Post&Comment");
            op=kb.nextLine();


            switch(op) {
                case "1"->{
                    String text="";
                    String line=null;
                    String op2=null;
                    System.out.println("---Write Post&Comment---");
                    System.out.println("> Select task: 1.Write Post | 2.Write Comment ");
                    op2=kb.nextLine();

                    //write post
                    if(op2.equals("1")) {
                        System.out.println("---Write Post---");
                        System.out.println("> Write post(Enter -1 if end): ");
                        // line write -> concat \n -> save to text
                        int firstLine=0;

                        while(true) {
                            line=kb.nextLine();
                            if(line.equals("-1")) break;
                            if(firstLine==0) {
                                text=text+line;
                                firstLine=1;
                            }
                            else {
                                text=text+"\r\n";
                                text=text+line;
                            }


                        }

                        //send to server
                        stmt=conn.createStatement();
                        sqlStat="INSERT INTO Posts VALUES(NULL, \""+text+"\", \""+userId+"\", CURRENT_TIMESTAMP())";
                        stmt.executeUpdate(sqlStat);

                        System.out.println("---Write Post Success!---");


                    }

                    //write comment
                    else if(op2.equals("2")) {
                        String textType=null;
                        int TID=0;
                        String writerOfTID=null;
                        int originalPostTID=0;

                        System.out.println("---Write Comment---");
                        //find type
                        System.out.print("> Enter Text Type you want to comment to(P:post C:comment): ");
                        textType=kb.nextLine();
                        System.out.print("> Enter TID you want to comment: ");
                        TID=Integer.parseInt(kb.nextLine());

                        //find writerOfTID
                        stmt=conn.createStatement();
                        //comment to post
                        if(textType.equalsIgnoreCase("P")) {
                            sqlStat="SELECT Writer_ID FROM Posts WHERE P_ID="+TID;
                            rs=stmt.executeQuery(sqlStat);
                            if(rs.next()) writerOfTID=rs.getString(1);
                            else System.out.println("*ERROR!: This TID does not exist");
                        }
                        //comment to comment
                        else if(textType.equalsIgnoreCase("C")) {
                            sqlStat="SELECT Commenter_ID FROM Comments WHERE C_ID="+TID;
                            rs=stmt.executeQuery(sqlStat);
                            if(rs.next()) writerOfTID=rs.getString(1);
                            else System.out.println("*ERROR!: This TID does not exist");
                        }



                        System.out.println("> Write Comment(Enter -1 if end): ");
                        //comment type must have prefix @~
                        text=text+"@"+writerOfTID+" ";
                        // line write -> concat \n -> save to text
                        int firstLine=0;

                        while(true) {
                            line=kb.nextLine();
                            if(line.equals("-1")) break;
                            if(firstLine==0) {
                                text=text+line;
                                firstLine=1;
                            }
                            else {
                                text=text+"\r\n";
                                text=text+line;
                            }

                        }


                        //send to server

                        //comment to post
                        if(textType.equalsIgnoreCase("P")) {
                            stmt=conn.createStatement();
                            sqlStat="INSERT INTO Comments VALUES(NULL,\""+text+"\", \""+userId+"\","+TID+", 0, CURRENT_TIMESTAMP())";
                            stmt.executeUpdate(sqlStat);
                        }
                        //comment to comment
                        else if(textType.equalsIgnoreCase("C")) {
                            //find original post
                            while(true) {
                                stmt=conn.createStatement();
                                sqlStat="SELECT P_ID,Parent_C_ID FROM Comments WHERE C_ID="+TID;
                                rs=stmt.executeQuery(sqlStat);
                                if(rs.next()) {
                                    //if this is first comment?
                                    if(rs.getInt(2)==0) {
                                        originalPostTID=rs.getInt(1);
                                        break;
                                    }
                                    //upup.. (actually dont need to... but recursive concept)
                                    else TID=rs.getInt(2);
                                }
                                else {
                                    System.out.println("*ERROR!: This TID does not exist");
                                    break;
                                }

                            }


                            stmt=conn.createStatement();
                            sqlStat="INSERT INTO Comments VALUES(NULL,\""+text+"\", \""+userId+"\","+originalPostTID+","+TID+", CURRENT_TIMESTAMP())";
                            stmt.executeUpdate(sqlStat);
                        }

                        System.out.println("---Write Comment Success!---");

                    }



                }
                case "2"->{

                    String textType=null;
                    int TID=0;

                    System.out.println("---Like Post&Comment---");
                    System.out.print("> Enter Text Type you want to like about(P:post C:comment): ");
                    textType=kb.nextLine();
                    System.out.print("> Enter TID you want to like: ");
                    TID=Integer.parseInt(kb.nextLine());


                    stmt=conn.createStatement();



                    //like about post
                    if(textType.equalsIgnoreCase("P")) {
                        //check if already like? (similar to follow/unfollow)
                        sqlStat="SELECT User_ID FROM Post_like WHERE P_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";

                    }

                    //like about comment
                    else if(textType.equalsIgnoreCase("C")) {
                        //check if already like? (similar to follow/unfollow)
                        sqlStat="SELECT User_ID FROM Comment_like WHERE C_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";
                    }

                    rs=stmt.executeQuery(sqlStat);

                    //can unlike
                    if(rs.next()) {
                        System.out.println("---Unlike---");

                        stmt=conn.createStatement();

                        if(textType.equalsIgnoreCase("P")) {
                            sqlStat="DELETE FROM Post_like WHERE P_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";
                        }
                        else if(textType.equalsIgnoreCase("C")) {
                            sqlStat="DELETE FROM Comment_like WHERE C_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";
                        }

                        stmt.executeUpdate(sqlStat);

                        System.out.println("---Unlike Success!---");


                    }
                    //can like
                    else {
                        System.out.println("---Like---");

                        stmt=conn.createStatement();

                        if(textType.equalsIgnoreCase("P")) {
                            sqlStat="INSERT INTO Post_like VALUES (NULL, "+TID+",\""+userId+"\")";
                        }
                        else if(textType.equalsIgnoreCase("C")) {
                            sqlStat="INSERT INTO Comment_like VALUES (NULL, "+TID+",\""+userId+"\")";
                        }

                        stmt.executeUpdate(sqlStat);

                        System.out.println("---Like Success!---");

                    }




                }
                //follow unfollow--switch,
                case "3"->{
                    String followedId=null;
                    System.out.println("---Follow/Unfollow---");
                    System.out.print("> Enter User ID you want to follow/unfollow: ");
                    followedId=kb.nextLine();

                    //match check1: exist user?
                    stmt=conn.createStatement();
                    sqlStat="SELECT User_ID FROM User WHERE User_ID=\""+followedId+"\"";
                    rs=stmt.executeQuery(sqlStat);

                    if(rs.next()) {
                        //match check2: check follow status

                        stmt=conn.createStatement();
                        sqlStat="SELECT Followed_ID FROM Follow WHERE Following_ID=\""+userId+"\""+" AND Followed_ID=\""+followedId+"\"";
                        rs=stmt.executeQuery(sqlStat);

                        //can unfollow
                        if(rs.next()) {
                            System.out.println("---Unfollow---");

                            stmt=conn.createStatement();
                            sqlStat="DELETE FROM Follow WHERE Following_ID=\""+userId+"\" AND Followed_ID=\""+followedId+"\"";
                            stmt.executeUpdate(sqlStat);

                            System.out.println("---Unfollow Success!---");


                        }
                        //can follow
                        else {
                            System.out.println("---Follow---");

                            stmt=conn.createStatement();
                            sqlStat="INSERT INTO Follow(F_ID, Following_ID,Followed_ID) VALUES (NULL, \""+userId+"\" , \""+followedId+"\" )";
                            stmt.executeUpdate(sqlStat);

                            System.out.println("---Follow Success!---");

                        }

                    }
                    else System.out.println("*ERROR!: This ID does not exist");


                }
                case "4"->{
                    String wantShowFollowId=null;

                    System.out.println("---Show Follower/Following list---");
                    System.out.print("> Enter User ID you want to see follower/following list: ");
                    wantShowFollowId=kb.nextLine();

                    //match check1: exist user?
                    stmt=conn.createStatement();
                    sqlStat="SELECT User_ID FROM User WHERE User_ID=\""+ wantShowFollowId+"\"";
                    rs=stmt.executeQuery(sqlStat);

                    //show list
                    if(rs.next()) {
                        //follower list
                        stmt=conn.createStatement();
                        sqlStat="SELECT Following_ID FROM FOLLOW WHERE Followed_ID=\""+wantShowFollowId+"\"";
                        rs=stmt.executeQuery(sqlStat);

                        System.out.println("---"+wantShowFollowId+"\' s Follower list---");
                        System.out.println("|---------------------------------------------");
                        while(rs.next()) {
                            System.out.println("|"+rs.getString(1));
                        }
                        System.out.println("|---------------------------------------------");

                        //following list
                        stmt=conn.createStatement();
                        sqlStat="SELECT Followed_ID FROM FOLLOW WHERE Following_ID=\""+wantShowFollowId+"\"";
                        rs=stmt.executeQuery(sqlStat);

                        System.out.println("---"+wantShowFollowId+"\' s Following list---");
                        System.out.println("|---------------------------------------------");
                        while(rs.next()) {
                            System.out.println("|"+rs.getString(1));
                        }

                        System.out.println("|---------------------------------------------");
                    }
                    else System.out.println("*ERROR!: This ID does not exist");


                }
                case "5"->{
                    String searchId=null;
                    String searchWord=null;
                    System.out.println("---Search Post&Comment---");
                    System.out.print("> Enter User ID you want to search its Post&Comment: ");
                    searchId=kb.nextLine();
                    System.out.print("> Enter Word you want to search: ");
                    searchWord=kb.nextLine();

                    //match check1: exist user?
                    stmt=conn.createStatement();
                    sqlStat="SELECT User_ID FROM User WHERE User_ID=\""+ searchId+"\"";
                    rs=stmt.executeQuery(sqlStat);

                    //make search string operation
                    String searchOp="%%"+searchWord+"%%";

                    //show search result in latest order
                    System.out.println("---Searched Post&Comment---");
                    System.out.println("|---------------------------------------------");
                    if(rs.next()) {

                        stmt=conn.createStatement();
                        sqlStat= "SELECT * FROM (\r\n"
                                + "(SELECT Text,Writer_ID,Date_time,P_ID,\"P\" FROM Posts WHERE TEXT LIKE \""+searchOp+"\" AND Writer_ID=\""+searchId+"\")\r\n"
                                + "UNION\r\n"
                                + "(SELECT Text,Commenter_ID,Date_time,C_ID,\"C\" FROM Comments WHERE TEXT LIKE \""+searchOp+"\" AND Commenter_ID=\""+searchId+"\") \r\n"
                                + ") AS lpc  \r\n"
                                + "ORDER BY lpc.Date_time DESC";

                        rs=stmt.executeQuery(sqlStat);
                        //display text
                        String Text=null;
                        String Writer_ID=null;
                        String Date_time=null;
                        String Tid=null;
                        String TextType=null;
                        int Like=0;
                        int Repost=0;

                        while(rs.next()) {
                            Text=rs.getString(1);
                            Writer_ID=rs.getString(2);
                            Date_time=rs.getString(3);
                            Tid=rs.getString(4);
                            TextType=rs.getString(5);

                            //aggregate like
                            stmt=conn.createStatement();
                            //if Post?
                            if(TextType.equalsIgnoreCase("P")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Post_like WHERE P_ID=\""+Tid+"\"";
                                ResultSet rs2=stmt.executeQuery(sqlStat);
                                if(rs2.next()) Like=rs2.getInt(1);

                            }
                            //if Comment?
                            if(TextType.equalsIgnoreCase("C")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID=\""+Tid+"\"";
                                ResultSet rs2=stmt.executeQuery(sqlStat);
                                if(rs2.next()) Like=rs2.getInt(1);

                            }


                            //aggregate repost
                            stmt=conn.createStatement();
                            //if Post?
                            if(TextType.equalsIgnoreCase("P")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID=\""+Tid+"\"";
                                ResultSet rs3=stmt.executeQuery(sqlStat);
                                if(rs3.next()) Repost=rs3.getInt(1);

                            }
                            //if Comment?
                            if(TextType.equalsIgnoreCase("C")) {

                                sqlStat="SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID=\""+Tid+"\"";
                                ResultSet rs3=stmt.executeQuery(sqlStat);
                                if(rs3.next()) Repost=rs3.getInt(1);

                            }


                            System.out.println("|---------------------------------------------");
                            System.out.println("|Writer: "+Writer_ID+" \t Datetime: "+Date_time+" \t TID: "+Tid);
                            System.out.println("|"+Text);
                            System.out.println("|Like: "+ Like+" \t Repost: "+ Repost);
                            System.out.println("|---------------------------------------------");

                        }
                    }

                    else System.out.println("*ERROR!: This ID does not exist");

                    System.out.println("|---------------------------------------------");



                }
                //Repost/Unrepost (later) <will go to this>
                case "6"->{
                    String textType=null;
                    int TID=0;
                    System.out.println("---Repost Post&Comment---");
                    System.out.print("> Enter Text Type you want to repost about(P:post C:comment): ");
                    textType=kb.nextLine();
                    System.out.print("> Enter TID you want to repost: ");
                    TID=Integer.parseInt(kb.nextLine());


                    stmt=conn.createStatement();

                    //repost about post
                    if(textType.equalsIgnoreCase("P")) {
                        //check if already repost? (similar to follow/unfollow)
                        sqlStat="SELECT User_ID FROM Repost_post WHERE P_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";

                    }

                    //repost about comment
                    else if(textType.equalsIgnoreCase("C")) {
                        //check if already repost? (similar to follow/unfollow)
                        sqlStat="SELECT User_ID FROM Repost_comment WHERE C_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";
                    }

                    rs=stmt.executeQuery(sqlStat);

                    //can unrepost
                    if(rs.next()) {
                        System.out.println("---Unrepost---");

                        stmt=conn.createStatement();

                        if(textType.equalsIgnoreCase("P")) {
                            sqlStat="DELETE FROM Repost_post WHERE P_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";
                        }
                        else if(textType.equalsIgnoreCase("C")) {
                            sqlStat="DELETE FROM Repost_comment WHERE C_ID=\""+TID+"\""+" AND User_ID=\""+userId+"\"";
                        }

                        stmt.executeUpdate(sqlStat);

                        System.out.println("---Unrepost Success!---");


                    }
                    //can repost
                    else {
                        System.out.println("---Repost---");

                        stmt=conn.createStatement();

                        if(textType.equalsIgnoreCase("P")) {
                            sqlStat="INSERT INTO Repost_post VALUES (NULL, "+TID+",\""+userId+"\")";
                        }
                        else if(textType.equalsIgnoreCase("C")) {
                            sqlStat="INSERT INTO Repost_comment VALUES (NULL, "+TID+",\""+userId+"\")";
                        }

                        stmt.executeUpdate(sqlStat);

                        System.out.println("---Repost Success!---");

                    }



                }
            }





            //Directed Messages (later) <will go to main >



        }catch(SQLException e2) {
            e2.printStackTrace();
        }


    }


    //connection&method call
    public static void main(String[] args) throws Exception{



        //connect to SQLserver
        conn=null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url="jdbc:mysql://localhost/twitter0";
            String user="root",passwd="0000";
            conn=DriverManager.getConnection(url,user,passwd);
            System.out.println(conn);
        }
        catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        //Sign up/Log in/change Password
        accessSLC();


        //connection close
        try {
            if(conn!=null && !conn.isClosed()) conn.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

}
