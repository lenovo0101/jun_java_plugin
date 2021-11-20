package dbutils;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.junit.Test;



public class LogDAO {

 
    @Test
    public void add() throws SQLException {
        //灏嗘暟鎹簮浼犻�掔粰QueryRunner锛孮ueryRunner鍐呴儴閫氳繃鏁版嵁婧愯幏鍙栨暟鎹簱杩炴帴
        QueryRunner qr = new QueryRunner(DBUtils.getDataSource());
        String sql = "insert into users(name,password,email,birthday) values(?,?,?,?)";
        Object params[] = {"1111","11122", "gacl@sina.com", new Date()};
        //Object params[] = {"鐧借檸绁炵殗","123", "gacl@sina.com", "1988-05-07"};
        qr.update(sql, params);
    }
    
    @Test
    public void delete() throws SQLException {

        QueryRunner qr = new QueryRunner(DBUtils.getDataSource());
        String sql = "delete from users where id=?";
        qr.update(sql, 1);

    }

    @Test
    public void update() throws SQLException {
        QueryRunner qr = new QueryRunner(DBUtils.getDataSource());
        String sql = "update users set name=? where id=?";
        Object params[] = { "ddd", 5};
        qr.update(sql, params);
    }

    @Test
    public void find() throws SQLException {
        QueryRunner qr = new QueryRunner(DBUtils.getDataSource());
        String sql = "select * from users where id=?";
        Object params[] = {2};
//        User user = (User) qr.query(sql, params, new BeanHandler(User.class));
//        System.out.println(user.getBirthday());
    }

    @Test
    public void getAll() throws SQLException {
        QueryRunner qr = new QueryRunner(DBUtils.getDataSource());
        String sql = "select * from users";
//        List list = (List) qr.query(sql, new BeanListHandler(User.class));
//        System.out.println(list.size());
    }

   
    @Test
    public void testBatch() throws SQLException {
        QueryRunner qr = new QueryRunner(DBUtils.getDataSource());
        String sql = "insert into users(name,password,email,birthday) values(?,?,?,?)";
        Object params[][] = new Object[10][];
        for (int i = 0; i < 10; i++) {
            params[i] = new Object[] { "aa" + i, "123", "aa@sina.com",
                    new Date() };
        }
        qr.batch(sql, params);
    }
    
    //鐢╠butils瀹屾垚澶ф暟鎹紙涓嶅缓璁敤锛�
    /***************************************************************************
     create table testclob
     (
         id int primary key auto_increment,
         resume text
     );
     **************************************************************************/
    @Test
    public void testclob() throws SQLException, IOException{
        QueryRunner runner = new QueryRunner(DBUtils.getDataSource());
        String sql = "insert into testclob(resume) values(?)";  //clob
        //杩欑鏂瑰紡鑾峰彇鐨勮矾寰勶紝鍏朵腑鐨勭┖鏍间細琚娇鐢ㄢ��%20鈥濅唬鏇�
        String path  = LogDAO.class.getClassLoader().getResource("data.xml").getPath();
        //灏嗏��%20鈥濇浛鎹㈠洖绌烘牸
        path = path.replaceAll("%20", " ");
        FileReader in = new FileReader(path);
        char[] buffer = new char[(int) new File(path).length()];
        in.read(buffer);
        SerialClob clob = new SerialClob(buffer);
        Object params[] = {clob};
        runner.update(sql, params);
    }
}