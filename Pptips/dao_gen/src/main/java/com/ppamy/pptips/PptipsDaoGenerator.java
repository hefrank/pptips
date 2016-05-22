/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ppamy.pptips;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Run it as a Java application (not Android).
 *
 * @author hefrank clone from Markus Junginger
 */
public class PptipsDaoGenerator {
    private final static int DBVERSION = 1;

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(DBVERSION, "com.ppamy.pptips.dbs.tips_dao");
        schema.enableKeepSectionsByDefault();
        addTipsTable(schema);

//        new DaoGenerator().generateAll(schema, "./app/src/main/java");
        new DaoGenerator().generateAll(schema, "./src-gen/");
    }

    /**
     * 备忘录数据表
     * */
    private static void addTipsTable(Schema schema) {
    	Entity appsInfo = schema.addEntity("Tips");
    	//id
    	appsInfo.addIdProperty().autoincrement();
    	/**创建时间*/
    	appsInfo.addLongProperty("date");
    	/**执行时间*/
    	appsInfo.addLongProperty("action_date");
    	/**经度*/
    	appsInfo.addStringProperty("longitude");
    	/**纬度*/
    	appsInfo.addStringProperty("latitude");
    	/**地名*/
    	appsInfo.addStringProperty("loc_name");
    	/**主题*/
    	appsInfo.addStringProperty("subject");
    	/**内容*/
    	appsInfo.addStringProperty("body");

    	appsInfo.addIntProperty("type");

    	//先不增加 ContentProvider
//    	cldBuddy.addContentProvider();
    }
}
