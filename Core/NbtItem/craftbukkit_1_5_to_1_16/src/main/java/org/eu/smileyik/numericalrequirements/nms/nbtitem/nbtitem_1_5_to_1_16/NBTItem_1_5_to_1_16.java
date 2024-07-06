package org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_5_to_1_16;

import org.bukkit.inventory.ItemStack;
import org.eu.smileyik.numericalrequirements.nms.nbtitem.AbstractNBTItem;

import java.util.UUID;

import static org.eu.smileyik.numericalrequirements.nms.nbtitem.nbtitem_1_5_to_1_16.NBTItemMethods.*;

public class NBTItem_1_5_to_1_16 extends AbstractNBTItem {

    public NBTItem_1_5_to_1_16(ItemStack item) {
        super(item);
    }

    @Override
    protected Object doGetNMSCopy(ItemStack item) {
        try {
            return NBTItemMethods.asNMSCopy.invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Object doGetTags(Object nmsCopy) {
        if (nmsCopy == null) return null;
        try {
            return (boolean) hasTag.invoke(nmsCopy) ? getTag.invoke(nmsCopy) : newNBTTagCompound.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected boolean doContainsKey(Object tags, String key) {
        try {
            return (boolean) hasKey.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void doPut(Object tags, String key, Object value) {
        try {
            if (value instanceof UUID) {
                if (setUUID == null) {
                    doPut(tags, key, ((UUID) value).toString());
                } else {
                    setUUID.invoke(tags, key, (UUID) value);
                }
            } else if (value instanceof String) {
                setString.invoke(tags, key, (String) value);
            } else if (value instanceof Integer) {
                setInt.invoke(tags, key, (int) value);
            } else if (value instanceof Double) {
                setDouble.invoke(tags, key, (double) value);
            } else if (value instanceof Boolean) {
                setBoolean.invoke(tags, key, (boolean) value);
            } else if (value instanceof Float) {
                setFloat.invoke(tags, key, (float) value);
            } else if (value instanceof Long) {
                setLong.invoke(tags, key, (long) value);
            } else if (value instanceof Short) {
                setShort.invoke(tags, key, (short) value);
            } else if (value instanceof Byte) {
                setByte.invoke(tags, key, (byte) value);
            } else if (value instanceof int[]) {
                setIntArray.invoke(tags, key, (int[]) value);
            } else if (value instanceof byte[]) {
                setByteArray.invoke(tags, key, (byte[]) value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doGetString(Object tags, String key) {
        try {
            return (String) getString.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Byte doGetByte(Object tags, String key) {
        try {
            return (byte) getByte.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Integer doGetInt(Object tags, String key) {
        try {
            return (int) getInt.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Short doGetShort(Object tags, String key) {
        try {
            return (short) getShort.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Long doGetLong(Object tags, String key) {
        try {
            return (long) getLong.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Double doGetDouble(Object tags, String key) {
        try {
            return (double) getDouble.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected Float doGetFloat(Object tags, String key) {
        try {
            return (float) getFloat.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected boolean doGetBoolean(Object tags, String key) {
        try {
            return (boolean) getBoolean.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected byte[] doGetByteArray(Object tags, String key) {
        try {
            return (byte[]) getByteArray.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected int[] doGetIntArray(Object tags, String key) {
        try {
            return (int[]) getIntArray.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String doSaveToString(Object copy) {
        if (copy == null) return null;
        try {
            return save.invoke(copy, newNBTTagCompound.newInstance()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ItemStack applyNBT(Object nmsCopy, Object tags) {
        try {
            setTag.invoke(nmsCopy, tags);
            return (ItemStack) asBukkitCopy.invoke(null, nmsCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void doRemove(Object tags, String key) {
        try {
            remove.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected UUID doGetUUID(Object tags, String key) {
        try {
            if (getUUID == null) {
                return UUID.fromString(doGetString(tags, key));
            }
            return (UUID) getUUID.invoke(tags, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
