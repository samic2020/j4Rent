package Funcoes;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author YOGA 510
 */
public class Db {
    static private Connection conn = VariaveisGlobais.conexao.conn;

    private NamedPreparedStatement Parameters(NamedPreparedStatement stm, Object[][] param) {
        try {
            for (Object[] item : param) {
                switch (item[0].toString().trim().toLowerCase()) {
                    case "int" : 
                        stm.setInt(item[1].toString(), (int) item[2]); 
                        break;
                    case "bigint":
                        stm.setInt(item[1].toString(), (Integer) item[2]);
                        break;
                    case "date":
                        stm.setDate(item[1].toString(), Dates.toSqlDate((java.util.Date) item[2]));
                        break;
                    case "string": 
                        stm.setString(item[1].toString(), (String) item[2]);
                        break;
                    case "decimal":
                        stm.setBigDecimal(item[1].toString(), (BigDecimal) item[2]);
                        break;
                    case "boolean":
                        stm.setBoolean(item[1].toString(), (Boolean) item[2]);
                        break;
                    case "float":
                        stm.setFloat(item[1].toString(), (Float) item[2]);
                        break;
                    case "double":
                        stm.setDouble(item[1].toString(), (Double) item[2]);
                        break;
                }
            }       
        } catch (SQLException e) { e.printStackTrace(); }
        
        return stm;
    }
    
    public ResultSet OpenTable(String sqlString, Object[][] param) {
        ResultSet hResult = null;
        
        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatement(conn, sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        if (param != null && param.length > 0) stm = Parameters(stm, param);
            
        try {
            hResult = stm.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return hResult;
    }
    
    public void CloseTable(ResultSet rs) {
        try { rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
    }
   
    public int RecordCount(ResultSet hrs) {
        int retorno = 0;
        try {
            int pos = hrs.getRow();
            hrs.last();
            retorno = hrs.getRow();
            hrs.beforeFirst();
            if (pos > 0) hrs.absolute(pos);
        } catch (SQLException e) {retorno = 0;}
        return retorno;
    }    

    public int CommandExecute(String sqlString, Object[][] param) {
        int hRetorno = 0;

        NamedPreparedStatement stm = null;
        try {
            stm = NamedPreparedStatement.prepareStatement(conn, sqlString);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if (param != null && param.length > 0) stm = Parameters(stm, param);

        try {
            hRetorno = stm.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return hRetorno;
    }

    public String CreateSqlText(String aFiels[][], String cTableName, String cWhere, String cTipo) {
        int i = 0;
        String cRet = "";
        String auxCpo = "";

        if (cTipo.equals("INSERT")) {
            cRet = "INSERT INTO " + cTableName + " (";
            auxCpo = "VALUES (";

            for (i=0;i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0] + ",";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    auxCpo += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") auxCpo += aFiels[i][1] + ",";
            }

            cRet = cRet.substring(0,cRet.length() -1) + ") "
                 + auxCpo.substring(0,auxCpo.length() - 1) + ")";

        } else if (cTipo.equals("UPDATE")) {
            cRet = "UPDATE " + cTableName + " SET ";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += aFiels[i][0] + "=";

                if (aFiels[i][2] == "C" || aFiels[i][2] == "D") {
                    cRet += "'" + aFiels[i][1] + "',";
                } else if (aFiels[i][2] == "N") {
                    cRet += aFiels[i][1] + ",";
                }
            }

            cRet = cRet.substring(0,cRet.length() - 1) + " WHERE " + cWhere;

        } else if (cTipo.equals("SELECT")) {
            cRet = "SELECT";

            for (i=0; i <= aFiels.length - 1; i++) {
                cRet += ((aFiels.equals("")) ? aFiels[i][0] : aFiels[i][1] + ", ");
            }

            cRet = cRet.substring(0, cRet.length() - 2) + " FROM " + cTableName
                 + ((cWhere.equals("")) ? " WHERE " + cWhere : ";");

        }

        return cRet;
    }
    
        public String ReadParameters(String cVar) throws SQLException {
        String rVar = null;
        Object[][] param = new Object[][] {
            {"string", "variavel", cVar.trim().toLowerCase()}
        };
        ResultSet hResult = OpenTable("SELECT variavel, conteudo, tipo FROM PARAMETROS WHERE LOWER(TRIM(variavel)) = :variavel;", param);

        if (hResult.first()) {
            rVar = hResult.getString("conteudo");
        }

        return rVar;
    }

    /**
     * GravarParametros
     */
    public boolean SaveParameters(String cVar[]) throws SQLException {
        boolean rVar = false;
        boolean bInsert = false;
        String sql = "";

        bInsert = (ReadParameters(cVar[0]) == null);
        Object[][] param = null;
        if (bInsert) {
            sql = "INSERT INTO PARAMETROS (variavel, tipo, conteudo) VALUES (:variavel, :tipo, :conteudo);";
            param = new Object[][] {
                {cVar[0], cVar[1], cVar[2]}  
            };
        } else {
            sql = "UPDATE PARAMETROS SET CONTEUDO = :conteudo WHERE VARIAVEL = :variavel;'";
            param = new Object[][] {
                {"string", "conteudo", cVar[2]},
                {"string", "variavel", cVar[0]}
            };
        }

        rVar = (CommandExecute(sql, param)) > 0;
        return rVar;
    }

    public Object[][] ReadFieldsTable(String[] aCampos, String tbNome, String sWhere) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos + " FROM " + tbNome + " WHERE " + sWhere + " LIMIT 0,1";
        ResultSet tmpResult = OpenTable(sSql, null);
        ResultSetMetaData md = tmpResult.getMetaData();
        Object[][] vRetorno = new Object[aCampos.length][4];
        int i = 0;
        
        while (tmpResult.next()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                String columnName = md.getColumnName(i + 1);
                String typeName = md.getColumnTypeName(i + 1);
                vRetorno[i][0] = columnName;
                vRetorno[i][1] =  typeName;

                // Work field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                } 
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }
                try {
                    switch (typeName.trim().toLowerCase()) {
                        case "varchar":
                            vRetorno[i][3] = tmpResult.getString(variavel);
                            break;
                        case "int":
                            vRetorno[i][3] = tmpResult.getInt(variavel);
                            break;
                        case "date":
                            vRetorno[i][3] = tmpResult.getDate(variavel);
                            break;
                        case "datetime":
                            vRetorno[i][3] = tmpResult.getDate(variavel);
                            break;
                        case "time":
                            vRetorno[i][3] = tmpResult.getTime(variavel);
                        default:
                            throw new AssertionError();
                    }                    
                } catch (NullPointerException ex) { vRetorno[i][3] = null; }
            }
        }
        CloseTable(tmpResult);

        if (i == 0) vRetorno = null;        
        return vRetorno;
    }

    public Object[][] ReadFieldsTable(String[] aCampos, String tbNome, String sWhere, Object[][] param) throws SQLException {
        String sCampos = FuncoesGlobais.join(aCampos,", ");
        String sSql = "SELECT " + sCampos + " FROM " + tbNome + " WHERE " + sWhere;
        ResultSet tmpResult = OpenTable(sSql, param);
        ResultSetMetaData md = tmpResult.getMetaData();
        String[][] vRetorno = new String[aCampos.length][4];
        int i = 0;

        while (tmpResult.next()) {
            for (i=0; i<= aCampos.length - 1; i++) {
                vRetorno[i][0] = md.getColumnName(i + 1);
                vRetorno[i][1] =  md.getColumnTypeName(i + 1);

                // Work field name
                String variavel = aCampos[i].trim();
                if (variavel.toLowerCase().contains(" as ")) {
                    variavel = variavel.substring(variavel.toLowerCase().indexOf(" as") + 3).trim();
                } 
                try {
                    vRetorno[i][2] =  String.valueOf(tmpResult.getString(variavel).length());
                } catch (NullPointerException ex) { vRetorno[i][2] = "0"; }
                try {
                    vRetorno[i][3] = tmpResult.getString(variavel);
                } catch (NullPointerException ex) { vRetorno[i][3] = ""; }
            }
        }
        
        CloseTable(tmpResult);

        if (i == 0) vRetorno = null;        
        return vRetorno;
    }
}
