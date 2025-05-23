/*
 * {{{ header & license
 * Copyright (c) 2007 Wisconsin Court System
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package com.openhtmltopdf.css.parser;

import com.openhtmltopdf.css.constants.IdentValue;
import com.openhtmltopdf.util.ArrayUtil;
import org.w3c.dom.DOMException;

import java.util.ArrayList;
import java.util.List;

public class PropertyValue implements CSSPrimitiveValue {
    public static final short VALUE_TYPE_NUMBER = 1;
    public static final short VALUE_TYPE_LENGTH = 2;
    public static final short VALUE_TYPE_COLOR = 3;
    public static final short VALUE_TYPE_IDENT = 4;
    public static final short VALUE_TYPE_STRING = 5;
    public static final short VALUE_TYPE_LIST = 6;
    public static final short VALUE_TYPE_FUNCTION = 7;
    public static final short VALUE_TYPE_COUNTERS = 8;
    
    private final short _type;
    private final short _cssValueType;
    
    private String _stringValue;
    private float _floatValue;
    private String[] _stringArrayValue;
    
    private final String _cssText;
    
    private FSColor _FSColor;
    
    private IdentValue _identValue;
    
    private short _propertyValueType;
    
    private Token _operator;
    
    private List<PropertyValue> _values;
    private List<CounterData> _counters;
    private FSFunction _function;

    public PropertyValue(short type, float floatValue, String cssText) {
        _type = type;
        _floatValue = floatValue;
        _cssValueType = CSSValue.CSS_PRIMITIVE_VALUE;
        _cssText = cssText;
        
        if (type == CSSPrimitiveValue.CSS_NUMBER && floatValue != 0.0f) {
            _propertyValueType = VALUE_TYPE_NUMBER;
        } else {
            _propertyValueType = VALUE_TYPE_LENGTH;
        }
    }
    
    public PropertyValue(FSColor color) {
        _type = CSSPrimitiveValue.CSS_RGBCOLOR;
        _cssValueType = CSSValue.CSS_PRIMITIVE_VALUE;
        _cssText = color.toString();
        _FSColor = color;
        
        _propertyValueType = VALUE_TYPE_COLOR;
    }
    
    public PropertyValue(short type, String stringValue, String cssText) {
        _type = type;
        _stringValue = stringValue;
        // Must be a case-insensitive compare since ident values aren't normalized
        // for font and font-family
        _cssValueType = _stringValue.equalsIgnoreCase("inherit") ? CSSValue.CSS_INHERIT : CSSValue.CSS_PRIMITIVE_VALUE;
        _cssText = cssText;
        
        if (type == CSSPrimitiveValue.CSS_IDENT) {
            _propertyValueType = VALUE_TYPE_IDENT;
        } else {
            _propertyValueType = VALUE_TYPE_STRING;
        }
    }
    
    public PropertyValue(IdentValue ident) {
        _type = CSSPrimitiveValue.CSS_IDENT;
        _stringValue = ident.toString();
        _cssValueType = _stringValue.equals("inherit") ? CSSValue.CSS_INHERIT : CSSValue.CSS_PRIMITIVE_VALUE;
        _cssText = ident.toString();
        
        _propertyValueType = VALUE_TYPE_IDENT;
        _identValue = ident;
    }
    
    public PropertyValue(List<PropertyValue> values) {
        _type = CSSPrimitiveValue.CSS_UNKNOWN; // HACK
        _cssValueType = CSSValue.CSS_CUSTOM;
        _cssText = values.toString(); // HACK
        
        _values = values;
        _propertyValueType = VALUE_TYPE_LIST;
    }
    
    public PropertyValue(List<CounterData> values, boolean unused) {
        _type = CSSPrimitiveValue.CSS_UNKNOWN; // HACK
        _cssValueType = CSSValue.CSS_CUSTOM;
        _cssText = values.toString(); // HACK
        
        _counters = values;
        _propertyValueType = VALUE_TYPE_COUNTERS;
    }
    
    public PropertyValue(FSFunction function) {
        _type = CSSPrimitiveValue.CSS_UNKNOWN;
        _cssValueType = CSSValue.CSS_CUSTOM;
        _cssText = function.toString();
        
        _function = function;
        _propertyValueType = VALUE_TYPE_FUNCTION;
    }

    @Override
    public float getFloatValue(short unitType) throws DOMException {
        return _floatValue;
    }
    
    public float getFloatValue() {
        return _floatValue;
    }

    @Override
    public short getPrimitiveType() {
        return _type;
    }

    @Override
    public String getStringValue() throws DOMException {
        return _stringValue;
    }

    public void setFloatValue(short unitType, float floatValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setStringValue(short stringType, String stringValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCssText() {
        return _cssText;
    }

    @Override
    public short getCssValueType() {
        return _cssValueType;
    }

    public void setCssText(String cssText) throws DOMException {
        throw new UnsupportedOperationException();
    }
    
    public FSColor getFSColor() {
        return _FSColor;
    }

    public IdentValue getIdentValue() {
        return _identValue;
    }

    public void setIdentValue(IdentValue identValue) {
        _identValue = identValue;
    }
    
    public short getPropertyValueType() {
        return _propertyValueType;
    }

    public Token getOperator() {
        return _operator;
    }

    public void setOperator(Token operator) {
        _operator = operator;
    }

    public String[] getStringArrayValue() {
        return ArrayUtil.cloneOrEmpty(_stringArrayValue);
    }

    public void setStringArrayValue(String[] stringArrayValue) {
        _stringArrayValue = ArrayUtil.cloneOrEmpty(stringArrayValue);
    }
    
    @Override
    public String toString() {
        return _cssText;
    }
    
    public List<PropertyValue> getValues() {
        if (_values == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(_values);
    }
    
    public List<CounterData> getCounters() {
        return new ArrayList<>(_counters);
    }
    
    public FSFunction getFunction() {
        return _function;
    }
    
    public String getFingerprint() {
        if (getPropertyValueType() == VALUE_TYPE_IDENT) {
            if (_identValue == null) {
                _identValue = IdentValue.getByIdentString(getStringValue());
            }
            return "I" + _identValue.FS_ID;
        } else {
            return getCssText();
        }
    }
}
