// Modifications copyright (C) 2017, Baidu.com, Inc.
// Copyright 2017 The Apache Software Foundation

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.baidu.palo.analysis;

import com.baidu.palo.cluster.ClusterNamespace;
import com.baidu.palo.common.AnalysisException;
import com.baidu.palo.common.ErrorCode;
import com.baidu.palo.common.ErrorReport;
import com.baidu.palo.common.FeNameFormat;
import com.baidu.palo.common.InternalException;
import com.google.common.base.Strings;

public class AlterDatabaseRename extends DdlStmt {
    private String dbName;
    private String newDbName;

    public AlterDatabaseRename(String dbName, String newDbName) {
        this.dbName = dbName;
        this.newDbName = newDbName;
    }

    public String getDbName() {
        return dbName;
    }

    public String getNewDbName() {
        return newDbName;
    }

    @Override
    public void analyze(Analyzer analyzer) throws AnalysisException, InternalException {
        super.analyze(analyzer);
        if (Strings.isNullOrEmpty(dbName)) {
            throw new AnalysisException("Database name is not set");
        }
        
        if (!analyzer.getCatalog().getUserMgr().isSuperuser(analyzer.getUser())) {
            ErrorReport.reportAnalysisException(ErrorCode.ERR_DB_ACCESS_DENIED, analyzer.getUser(), dbName);
        }

        if (Strings.isNullOrEmpty(newDbName)) {
            throw new AnalysisException("New database name is not set");
        }
        
        FeNameFormat.checkDbName(newDbName);
        
        dbName = ClusterNamespace.getFullName(getClusterName(), dbName);
        newDbName = ClusterNamespace.getFullName(getClusterName(), newDbName);
    }

    @Override
    public String toSql() {
        return "ALTER DATABASE " + dbName + " RENAME " + newDbName;
    }

}
