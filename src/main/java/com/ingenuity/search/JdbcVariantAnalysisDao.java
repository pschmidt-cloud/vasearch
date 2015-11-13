package com.ingenuity.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pschmidt
 */
public class JdbcVariantAnalysisDao extends JdbcTemplate {
    private static final Log log = LogFactory.getLog(JdbcVariantAnalysisDao.class);

    public class Sample {
        private int id;
        private String name;
        private int genome;
        private int gender;
        private int status;
        private int variants;
        private Timestamp timeCreated;
        private Map<String, String> annotations = new HashMap<String, String>();

        public void addAnnotation(String name, String value) {
            //name = name.replace(" ", "_");
            annotations.put(name.trim().toLowerCase(), value.trim().toLowerCase());
        }

        public Map<String, String> getAnnotations() {
            return annotations;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getGenome() {
            return genome;
        }

        public Timestamp getTimeCreated() {
            return timeCreated;
        }

        public void setTimeCreated(Timestamp timeCreated) {
            this.timeCreated = timeCreated;
        }

        public void setGenome(int genome) {
            this.genome = genome;
        }

        public int getVariants() {
            return variants;
        }

        public void setVariants(int variants) {
            this.variants = variants;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public class ItemProperty {
        private int type;
        private int item;
        private String name;
        private String value;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getItem() {
            return item;
        }

        public void setItem(int item) {
            this.item = item;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public Sample selectSample(int id) throws Exception {
        try {
            String sql = "select * from iva_sample where id=?";
            List<Sample> values = query(sql, new ResultSetExtractor<List<Sample>>() {
                @Override
                public List<Sample> extractData(ResultSet rs) throws SQLException, DataAccessException {
                    return null;
                }
            });
            return ((values.size() > 0) ? values.get(0) : null);
        } catch (DataAccessException e) {
            String msg = "Unable to select Sample data";
            log.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    public List<Sample> selectAllSamples(int minId, int maxId) throws Exception {
        setFetchSize(1000);

        try {
            String sql = "select s.id, s.name, s.genome, s.gender, s.status, s.variants, s.ctime, p.name as prop_name, p.value as prop_value " +
                "from iva_sample s, iva_item_property p where p.type=1 and p.id=s.id and s.id between " + minId + " and " + maxId;

            List<Sample> values = query(sql, new ResultSetExtractor<List<Sample>>() {
                @Override
                public List<Sample> extractData(ResultSet rs) throws SQLException, DataAccessException {
                    Map<Integer, Sample> map = new HashMap<Integer, Sample>();
                    Sample sample = null;
                    while (rs.next()) {
                        Integer id = rs.getInt("id");
                        sample = map.get(id);
                        if (sample == null) {
                            sample = getSample(rs);
                            map.put(id, sample);
                        }
                        sample.addAnnotation(rs.getString("prop_name"), rs.getString("prop_value"));
                    }
                    return new ArrayList<Sample>(map.values());
                }
            });

            return values;
        } catch (DataAccessException e) {
            String msg = "Unable to select Sample data";
            log.error(msg, e);
            throw new Exception(msg, e);
        }
    }

    private Sample getSample(ResultSet rs) throws SQLException {
        Sample sample = new Sample();

        sample.setId(rs.getInt("id"));
        sample.setName(rs.getString("name"));
        sample.setVariants(rs.getInt("variants"));
        sample.setGenome(rs.getInt("genome"));
        sample.setGender(rs.getInt("gender"));
        sample.setStatus(rs.getInt("status"));
        if (rs.getTimestamp("ctime") != null) {
            sample.setTimeCreated(rs.getTimestamp("ctime"));
        }

        return sample;
    }

    public List<ItemProperty> selectProperties(long id) throws SQLException {
        setFetchSize(1000);
        final String sql = "select /*+ PARALLEL  */ * from iva_item_property where type=1 and id=?";

        List<ItemProperty> values = query(sql, new ParameterizedRowMapper<ItemProperty>() {
            public ItemProperty mapRow(ResultSet rs, int row) throws SQLException {
                ItemProperty data = getProperties(rs);
                return data;
            }
        }, id);
        return (values);
    }

    private ItemProperty getProperties(ResultSet rs) throws SQLException {
        ItemProperty itemProperty = new ItemProperty();

        itemProperty.setName(rs.getString("name").toLowerCase());
        itemProperty.setValue(rs.getString("value").toLowerCase());
        itemProperty.setType(rs.getInt("type"));
        itemProperty.setItem(rs.getInt("item"));

        return itemProperty;
    }
}
