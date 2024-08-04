package org.eu.smileyik.numericalrequirements.core.item.relational;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "nreq_items")
public class ItemEntity {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField
    private boolean sync = true;
    @DatabaseField(dataType = DataType.LONG_STRING)
    private String item;

    public ItemEntity() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public boolean isSync() {
        return sync;
    }
}
