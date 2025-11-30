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
                        sqlStat = "SELECT Password FROM User WHERE User_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, id);//bind user Id
                            try (ResultSet rs = ps.executeQuery()) {
                                // match check
                                if (rs.next()) {
                                    String dbPw = rs.getString(1);
                                    if (pw.equals(dbPw)) {
                                        System.out.println("---Log in Success!---");
                                        // now we can access various tasks
                                        login = true;
                                        userId = id;
                                        userPw = pw;
                                    } else {
                                        System.out.println("*ERROR!: This Password is wrong");
                                    }

                                } else {
                                    System.out.println("*ERROR!: This ID does not exist");
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


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
                        sqlStat = "SELECT Password FROM User WHERE User_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, id);//bind user Id
                            try (ResultSet rs = ps.executeQuery()) {

                                if (rs.next()) {
                                    String dbPw=rs.getString(1);
                                    if (existPw.equals(dbPw)) {
                                        //if match, ask for new password
                                        System.out.println("> Enter New Password(max 15): ");
                                        newPw=kb.nextLine();

                                        //update password
                                        String updateSql="UPDATE User SET Password = ? WHERE User_ID = ?";
                                        try (PreparedStatement ups=conn.prepareStatement(updateSql)) {
                                            ups.setString(1, newPw);
                                            ups.setString(2, id);
                                            ups.executeUpdate();
                                        }

                                        System.out.println("---Change password Success!---");
                                    } else {
                                        System.out.println("*ERROR!: Existing Password is wrong");
                                    }
                                } else {
                                    System.out.println("*ERROR!: This ID does not exist");
                                }
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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

                        boolean exists = false;
                        //prepare sql
                        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, wantId);
                            rs = ps.executeQuery();
                            exists = rs.next();
                        }

                        //match check
                        if(exists) {
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


    public static void sendDM() throws Exception {
        System.out.println("---Send Direct Message---");
        System.out.print("> Enter Receiver ID: ");
        String recvId=kb.nextLine();

        //self-DM check
        if (recvId.equals(userId)) {
            System.out.println("*ERROR!: You can't send DM to yourself");
            return;
        }

        try {
            //check receiver exists
            String checkSql = "SELECT User_ID FROM User WHERE User_ID = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql);
                 ResultSet rs = ps.executeQuery()) {

                ps.setString(1, recvId);
                if (!rs.next()) {
                    System.out.println("*ERROR!: This ID does not exist");
                    return;
                }
            }

            //input message (multi-line)
            System.out.println("> Write DM (Enter -1 if end): ");
            String dmText = "";
            int firstLine = 0;
            while (true) {
                String line = kb.nextLine();
                if (line.equals("-1")) break;
                if (firstLine == 0) {
                    dmText += line;
                    firstLine = 1;
                } else {
                    dmText += "\r\n" + line;
                }
            }

            //insert DM
            String insertSql = "INSERT INTO Direct_message " +
                    "(DM_ID, Text, Sender_ID, Receiver_ID, Receiver_read, DATE_time) " +
                    "VALUES (NULL, ?, ?, ?, 0, CURRENT_TIMESTAMP())";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, dmText);
                ps.setString(2, userId);
                ps.setString(3, recvId);
                ps.executeUpdate();
            }

            System.out.println("---Send DM Success!---");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void showInbox() throws Exception {
        System.out.println("---Inbox (Received DMs)---");

        try {

            String selectSql = "SELECT DM_ID, Text, Sender_ID, DATE_time, Receiver_read " +
                    "FROM Direct_message " +
                    "WHERE Receiver_ID = ? " +
                    "ORDER BY DATE_time DESC";
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setString(1, userId);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean hasAny = false;

                    while (rs.next()) {
                        hasAny = true;
                        int dmId = rs.getInt(1);
                        String text = rs.getString(2);
                        String sender = rs.getString(3);
                        String dateTime = rs.getString(4);
                        int readFlag = rs.getInt(5);

                        String readMark = (readFlag == 0) ? "[UNREAD]" : "[READ]";

                        System.out.println("|---------------------------------------------");
                        System.out.println("|DM_ID: " + dmId + " " + readMark);
                        System.out.println("|From: " + sender + " \t Datetime: " + dateTime);
                        System.out.println("|" + text);
                        System.out.println("|---------------------------------------------");
                    }

                    if (!hasAny) {
                        System.out.println("| No messages.");
                        System.out.println("|---------------------------------------------");
                    }
                }
            }

            //auto mark as read
            String updateSql = "UPDATE Direct_message " +
                    "SET Receiver_read = 1 " +
                    "WHERE Receiver_ID = ? AND Receiver_read = 0";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, userId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //other core tasks; write post, like post...etc
    public static void coreTask() throws Exception{


        //other core tasks

        //resetting
        Statement stmt=null;
        PreparedStatement pstm=null;
        String sqlStat=null;
        String op=null;


        try {

            //write post-> dont need to writer id again  comment->need to parent post's writer id
            System.out.println("> Select task: 1.Write Post&Comment | 2.Like Post&Comment | 3.Follow/Unfollow | 4.Show Follower/Following list | 5.Search Post&Comment | 6.Repost Post&Comment | 7.Direct Message");
            op=kb.nextLine();


            switch(op) {
                case "1"->{
                    String line=null;
                    String op2=null;
                    System.out.println("---Write Post&Comment---");
                    System.out.println("> Select task: 1.Write Post | 2.Write Comment ");
                    op2=kb.nextLine();

                    //write post
                    if(op2.equals("1")) {
                        String postText="";
                        String subop=null;
                        String targetId=null;
                        System.out.println("---Write Post---");
                        System.out.println("> Selcet task: 1. Write post on my board | 2.Write on following's board(@ID)");

                        subop=kb.nextLine();
                        // line write -> concat \n -> save to text
                        int firstLine=0;

                        // if subop = 1 pass

                        //write on following's board
                        if (subop.equals("2")) {
                            System.out.print("> Enter following's ID: ");
                            targetId = kb.nextLine();

                            //self-mention check
                            if (targetId.equals(userId)) {
                                System.out.println("*ERROR!: You can't write @message to yourself");
                                return;
                            }

                            //check if actually following (query Follow table)
                            sqlStat = "SELECT Followed_ID FROM Follow " +
                                    "WHERE Following_ID = ? AND Followed_ID = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                                ps.setString(1, userId);
                                ps.setString(2, targetId);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (!rs.next()) {
                                        System.out.println("*ERROR!: You are not following this user");
                                        return;
                                    }
                                }
                            }


                            //add @ prefix to ID
                            postText = "@" + targetId + " ";
                            firstLine = 1;
                        }

                        System.out.println("> Write post(Enter -1 if end): ");
                        while(true) {
                            line=kb.nextLine();
                            if(line.equals("-1")) break;
                            if(firstLine==0) {
                                postText=postText+line;
                                firstLine=1;
                            }
                            else {
                                postText=postText+"\r\n";
                                postText=postText+line;
                            }


                        }

                        //send to server
                        sqlStat = "INSERT INTO Posts (Text, Writer_ID, Date_time) " +
                                "VALUES (?, ?, CURRENT_TIMESTAMP())";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, postText);
                            ps.setString(2, userId);
                            ps.executeUpdate();
                        }

                        System.out.println("---Write Post Success!---");


                    }

                    //write comment
                    else if(op2.equals("2")) {
                        String commentText="";
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
                        if (textType.equalsIgnoreCase("P")) { //comment to post
                            sqlStat = "SELECT Writer_ID FROM Posts WHERE P_ID = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                                ps.setInt(1, TID);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) {
                                        writerOfTID = rs.getString(1);
                                    } else {
                                        System.out.println("*ERROR!: This TID does not exist");
                                        return;
                                    }
                                }
                            }
                        }
                        else if (textType.equalsIgnoreCase("C")) { //comment to comment
                            sqlStat = "SELECT Commenter_ID FROM Comments WHERE C_ID = ?";
                            try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                                ps.setInt(1, TID);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) {
                                        writerOfTID = rs.getString(1);
                                    } else {
                                        System.out.println("*ERROR!: This TID does not exist");
                                        return;
                                    }
                                }
                            }
                        } else {
                            System.out.println("*ERROR!: Text Type must be P or C");
                            return;
                        }



                        System.out.println("> Write Comment(Enter -1 if end): ");
                        //comment type must have prefix @~
                        commentText=commentText+"@"+writerOfTID+" ";
                        // line write -> concat \n -> save to text
                        int firstLine=0;

                        while(true) {
                            line=kb.nextLine();
                            if(line.equals("-1")) break;
                            if(firstLine==0) {
                                commentText=commentText+line;
                                firstLine=1;
                            }
                            else {
                                commentText=commentText+"\r\n";
                                commentText=commentText+line;
                            }

                        }


                        //send to server

                        //comment to post
                        if (textType.equalsIgnoreCase("P")) {
                            sqlStat = "INSERT INTO Comments " +
                                    "(Text, Commenter_ID, P_ID, Parent_C_ID, Date_time) " +
                                    "VALUES (?, ?, ?, 0, CURRENT_TIMESTAMP())";
                            try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                                ps.setString(1, commentText);
                                ps.setString(2, userId);
                                ps.setInt(3, TID);
                                ps.executeUpdate();
                            }
                        }
                        //comment to comment
                        else if(textType.equalsIgnoreCase("C")) {
                            //find original post
                            while(true) {
                                sqlStat = "SELECT P_ID, Parent_C_ID FROM Comments WHERE C_ID = ?";
                                try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                                    ps.setInt(1, TID);
                                    try (ResultSet rs = ps.executeQuery()) {
                                        if (rs.next()) {
                                            int parentCid = rs.getInt(2);//if this is first comment?
                                            if (parentCid == 0) {
                                                originalPostTID = rs.getInt(1);
                                                break;
                                            } else {
                                                TID = parentCid; // go up to parent comment
                                            }
                                        } else {
                                            System.out.println("*ERROR!: This TID does not exist");
                                            return;
                                        }
                                    }
                                }
                            }


                            sqlStat = "INSERT INTO Comments " +
                                    "(Text, Commenter_ID, P_ID, Parent_C_ID, Date_time) " +
                                    "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP())";
                            try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                                ps.setString(1, commentText);
                                ps.setString(2, userId);
                                ps.setInt(3, originalPostTID);
                                ps.setInt(4, TID); // parent comment ID
                                ps.executeUpdate();
                            }
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


                    //validate textType
                    if (!textType.equalsIgnoreCase("P") && !textType.equalsIgnoreCase("C")) {
                        System.out.println("*ERROR!: Text Type must be P or C");
                        return;
                    }

                    //check already like
                    if (textType.equalsIgnoreCase("P")) {
                        sqlStat = "SELECT User_ID FROM Post_like WHERE P_ID = ? AND User_ID = ?";
                    } else { //C
                        sqlStat = "SELECT User_ID FROM Comment_like WHERE C_ID = ? AND User_ID = ?";
                    }

                    try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                        ps.setInt(1, TID);
                        ps.setString(2, userId);

                        try (ResultSet rs = ps.executeQuery()) {

                            // can unlike
                            if (rs.next()) {
                                System.out.println("---Unlike---");

                                if (textType.equalsIgnoreCase("P")) {
                                    sqlStat = "DELETE FROM Post_like WHERE P_ID = ? AND User_ID = ?";
                                } else {
                                    sqlStat = "DELETE FROM Comment_like WHERE C_ID = ? AND User_ID = ?";
                                }

                                try (PreparedStatement del = conn.prepareStatement(sqlStat)) {
                                    del.setInt(1, TID);
                                    del.setString(2, userId);
                                    del.executeUpdate();
                                }

                                System.out.println("---Unlike Success!---");
                            }

                            // can like
                            else {
                                System.out.println("---Like---");

                                if (textType.equalsIgnoreCase("P")) {
                                    sqlStat = "INSERT INTO Post_like (PL_ID, P_ID, User_ID) VALUES (NULL, ?, ?)";
                                } else {
                                    sqlStat = "INSERT INTO Comment_like (CL_ID, C_ID, User_ID) VALUES (NULL, ?, ?)";
                                }

                                try (PreparedStatement ins = conn.prepareStatement(sqlStat)) {
                                    ins.setInt(1, TID);
                                    ins.setString(2, userId);
                                    ins.executeUpdate();
                                }

                                System.out.println("---Like Success!---");
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
                //follow unfollow--switch,
                case "3"->{
                    String followedId=null;
                    System.out.println("---Follow/Unfollow---");
                    System.out.print("> Enter User ID you want to follow/unfollow: ");
                    followedId=kb.nextLine();

                    //self-follow check
                    if (followedId.equals(userId)) {
                        System.out.println("*ERROR!: You can't follow yourself");
                        return;
                    }

                    try {
                        //check user exists
                        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, followedId);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("*ERROR!: This ID does not exist");
                                    return;
                                }
                            }
                        }

                        //check follow status
                        sqlStat = "SELECT Followed_ID FROM Follow WHERE Following_ID = ? AND Followed_ID = ?";
                        boolean alreadyFollowing;
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, userId);
                            ps.setString(2, followedId);
                            try (ResultSet rs = ps.executeQuery()) {
                                alreadyFollowing = rs.next();
                            }
                        }

                        //toggle follow / unfollow
                        if (alreadyFollowing) {
                            System.out.println("---Unfollow---");

                            sqlStat = "DELETE FROM Follow WHERE Following_ID = ? AND Followed_ID = ?";
                            try (PreparedStatement del = conn.prepareStatement(sqlStat)) {
                                del.setString(1, userId);
                                del.setString(2, followedId);
                                del.executeUpdate();
                            }

                            System.out.println("---Unfollow Success!---");
                        } else {
                            System.out.println("---Follow---");

                            sqlStat = "INSERT INTO Follow (F_ID, Following_ID, Followed_ID) " +
                                    "VALUES (NULL, ?, ?)";
                            try (PreparedStatement ins = conn.prepareStatement(sqlStat)) {
                                ins.setString(1, userId);
                                ins.setString(2, followedId);
                                ins.executeUpdate();
                            }

                            System.out.println("---Follow Success!---");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
                case "4"->{
                    String wantShowFollowId=null;

                    System.out.println("---Show Follower/Following list---");
                    System.out.print("> Enter User ID you want to see follower/following list: ");
                    wantShowFollowId=kb.nextLine();

                    try {
                        // match check1: exist user?
                        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, wantShowFollowId);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("*ERROR!: This ID does not exist");
                                    return;
                                }
                            }
                        }

                        // show list

                        // follower list
                        sqlStat = "SELECT Following_ID FROM Follow WHERE Followed_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, wantShowFollowId);
                            try (ResultSet rs = ps.executeQuery()) {
                                System.out.println("---" + wantShowFollowId + "' s Follower list---");
                                System.out.println("|---------------------------------------------");
                                while (rs.next()) {
                                    System.out.println("|" + rs.getString(1));
                                }
                                System.out.println("|---------------------------------------------");
                            }
                        }

                        // following list
                        sqlStat = "SELECT Followed_ID FROM Follow WHERE Following_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, wantShowFollowId);
                            try (ResultSet rs = ps.executeQuery()) {
                                System.out.println("---" + wantShowFollowId + "' s Following list---");
                                System.out.println("|---------------------------------------------");
                                while (rs.next()) {
                                    System.out.println("|" + rs.getString(1));
                                }
                                System.out.println("|---------------------------------------------");
                            }
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }


                }
                case "5"->{
                    String searchId=null;
                    String searchWord=null;
                    System.out.println("---Search Post&Comment---");
                    System.out.print("> Enter User ID you want to search its Post&Comment: ");
                    searchId=kb.nextLine();
                    System.out.print("> Enter Word you want to search: ");
                    searchWord=kb.nextLine();


                    try {
                        // match check1: exist user?
                        sqlStat = "SELECT User_ID FROM User WHERE User_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, searchId);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("*ERROR!: This ID does not exist");
                                    System.out.println("|---------------------------------------------");
                                    return;
                                }
                            }
                        }

                        // make search string operation
                        String searchOp = "%" + searchWord + "%";

                        // show search result in latest order
                        System.out.println("---Searched Post&Comment---");
                        System.out.println("|---------------------------------------------");

                        sqlStat =
                                "SELECT * FROM ( \r\n" +
                                        "  (SELECT Text, Writer_ID, Date_time, P_ID, \"P\" " +
                                        "     FROM Posts WHERE Text LIKE ? AND Writer_ID = ?) \r\n" +
                                        "  UNION \r\n" +
                                        "  (SELECT Text, Commenter_ID, Date_time, C_ID, \"C\" " +
                                        "     FROM Comments WHERE Text LIKE ? AND Commenter_ID = ?) \r\n" +
                                        ") AS lpc \r\n" +
                                        "ORDER BY lpc.Date_time DESC";

                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setString(1, searchOp);  // Posts.TEXT LIKE ?
                            ps.setString(2, searchId);  // Posts.Writer_ID = ?
                            ps.setString(3, searchOp);  // Comments.TEXT LIKE ?
                            ps.setString(4, searchId);  // Comments.Commenter_ID = ?

                            try (ResultSet rs = ps.executeQuery()) {
                                String Text = null;
                                String Writer_ID = null;
                                String Date_time = null;
                                String Tid = null;
                                String TextType = null;
                                int Like = 0;
                                int Repost = 0;

                                while (rs.next()) {
                                    Text = rs.getString(1);
                                    Writer_ID = rs.getString(2);
                                    Date_time = rs.getString(3);
                                    Tid = rs.getString(4);
                                    TextType = rs.getString(5);

                                    // aggregate like
                                    Like = 0;
                                    if (TextType.equalsIgnoreCase("P")) {
                                        String likeSql = "SELECT COUNT(User_ID) FROM Post_like WHERE P_ID = ?";
                                        try (PreparedStatement likePs = conn.prepareStatement(likeSql)) {
                                            likePs.setInt(1, Integer.parseInt(Tid));
                                            try (ResultSet rs2 = likePs.executeQuery()) {
                                                if (rs2.next()) Like = rs2.getInt(1);
                                            }
                                        }
                                    } else if (TextType.equalsIgnoreCase("C")) {
                                        String likeSql = "SELECT COUNT(User_ID) FROM Comment_like WHERE C_ID = ?";
                                        try (PreparedStatement likePs = conn.prepareStatement(likeSql)) {
                                            likePs.setInt(1, Integer.parseInt(Tid));
                                            try (ResultSet rs2 = likePs.executeQuery()) {
                                                if (rs2.next()) Like = rs2.getInt(1);
                                            }
                                        }
                                    }

                                    // aggregate repost
                                    Repost = 0;
                                    if (TextType.equalsIgnoreCase("P")) {
                                        String repostSql = "SELECT COUNT(User_ID) FROM Repost_post WHERE P_ID = ?";
                                        try (PreparedStatement rpPs = conn.prepareStatement(repostSql)) {
                                            rpPs.setInt(1, Integer.parseInt(Tid));
                                            try (ResultSet rs3 = rpPs.executeQuery()) {
                                                if (rs3.next()) Repost = rs3.getInt(1);
                                            }
                                        }
                                    } else if (TextType.equalsIgnoreCase("C")) {
                                        String repostSql = "SELECT COUNT(User_ID) FROM Repost_comment WHERE C_ID = ?";
                                        try (PreparedStatement rpPs = conn.prepareStatement(repostSql)) {
                                            rpPs.setInt(1, Integer.parseInt(Tid));
                                            try (ResultSet rs3 = rpPs.executeQuery()) {
                                                if (rs3.next()) Repost = rs3.getInt(1);
                                            }
                                        }
                                    }

                                    System.out.println("|---------------------------------------------");
                                    System.out.println("|Writer: " + Writer_ID + " \t Datetime: " + Date_time + " \t TID: " + Tid);
                                    System.out.println("|" + Text);
                                    System.out.println("|Like: " + Like + " \t Repost: " + Repost);
                                    System.out.println("|---------------------------------------------");
                                }
                            }
                        }

                        System.out.println("|---------------------------------------------");

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }



                }
                //Repost/Unrepost
                case "6"->{
                    String textType=null;
                    int TID=0;
                    System.out.println("---Repost Post&Comment---");
                    System.out.print("> Enter Text Type you want to repost about(P:post C:comment): ");
                    textType=kb.nextLine();
                    System.out.print("> Enter TID you want to repost: ");
                    TID=Integer.parseInt(kb.nextLine());


                    //check if textType correct
                    if (!textType.equalsIgnoreCase("P") && !textType.equalsIgnoreCase("C")) {
                        System.out.println("*ERROR!: Text Type must be P or C");
                        return;
                    }


                    try {

                        //check if target TID exists
                        if (textType.equalsIgnoreCase("P")) {
                            sqlStat = "SELECT P_ID FROM Posts WHERE P_ID = ?";
                        } else { // C
                            sqlStat = "SELECT C_ID FROM Comments WHERE C_ID = ?";
                        }

                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setInt(1, TID);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("*ERROR!: This TID does not exist");
                                    return;
                                }
                            }
                        }

                        //check already repost
                        if (textType.equalsIgnoreCase("P")) {
                            sqlStat = "SELECT User_ID FROM Repost_post WHERE P_ID = ? AND User_ID = ?";
                        } else { // C
                            sqlStat = "SELECT User_ID FROM Repost_comment WHERE C_ID = ? AND User_ID = ?";
                        }

                        boolean alreadyReposted;
                        try (PreparedStatement ps = conn.prepareStatement(sqlStat)) {
                            ps.setInt(1, TID);
                            ps.setString(2, userId);
                            try (ResultSet rs = ps.executeQuery()) {
                                alreadyReposted = rs.next();
                            }
                        }

                        //can unrepost
                        if (alreadyReposted) {
                            System.out.println("---Unrepost---");

                            if (textType.equalsIgnoreCase("P")) {
                                sqlStat = "DELETE FROM Repost_post WHERE P_ID = ? AND User_ID = ?";
                            } else {
                                sqlStat = "DELETE FROM Repost_comment WHERE C_ID = ? AND User_ID = ?";
                            }

                            try (PreparedStatement del = conn.prepareStatement(sqlStat)) {
                                del.setInt(1, TID);
                                del.setString(2, userId);
                                del.executeUpdate();
                            }

                            System.out.println("---Unrepost Success!---");
                        }

                        //can repost
                        else {
                            System.out.println("---Repost---");

                            if (textType.equalsIgnoreCase("P")) {
                                sqlStat = "INSERT INTO Repost_post (RT_ID, P_ID, User_ID) VALUES (NULL, ?, ?)";
                            } else {
                                sqlStat = "INSERT INTO Repost_comment (RT_ID, C_ID, User_ID) VALUES (NULL, ?, ?)";
                            }

                            try (PreparedStatement ins = conn.prepareStatement(sqlStat)) {
                                ins.setInt(1, TID);
                                ins.setString(2, userId);
                                ins.executeUpdate();
                            }

                            System.out.println("---Repost Success!---");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }



                }
                //Directed Messages
                case"7"-> {
                    System.out.println("---Direct Message---");
                    System.out.println("> Select task: 1.Send DM | 2.Show inbox ");
                    String subop = kb.nextLine();
                    if (subop.equals("1")) {
                        sendDM();
                    } else if (subop.equals("2")) {
                        showInbox();
                    } else {
                        System.out.println("*ERROR!: Invalid DM menu");
                    }
                }
            }
        }

        catch(SQLException e2) {
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
