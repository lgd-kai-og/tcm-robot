#!/bin/bash

# 知识库更新脚本
# 定时从中央知识库服务器获取最新的中医知识数据

echo "==============================================="
echo "中医知识库更新开始 - $(date '+%Y-%m-%d %H:%M:%S')"
echo "==============================================="

# 知识库路径设置
KNOWLEDGE_DIR="/tcm_knowledge"
TEMP_DIR="/tmp/tcm_update"
DB_FILE="${KNOWLEDGE_DIR}/tcm_knowledge.db"
TEMP_DB_FILE="${TEMP_DIR}/new_knowledge.db"
BACKUP_DIR="${KNOWLEDGE_DIR}/backup"
VERSION_FILE="${KNOWLEDGE_DIR}/version.json"

# 确保目录存在
mkdir -p ${KNOWLEDGE_DIR}
mkdir -p ${TEMP_DIR}
mkdir -p ${BACKUP_DIR}

# 获取当前版本信息
current_version="0"
if [ -f "${VERSION_FILE}" ]; then
    current_version=$(jq -r '.version' ${VERSION_FILE})
    echo "当前知识库版本: ${current_version}"
else
    echo "未找到版本信息文件，将从版本0开始更新"
fi

# 从API获取最新版本信息
echo "正在检查更新..."
api_response=$(curl -s -S "https://tcm-db.cn/api/knowledge/version")
latest_version=$(echo ${api_response} | jq -r '.version')
update_url=$(echo ${api_response} | jq -r '.download_url')

if [ "${latest_version}" == "${current_version}" ]; then
    echo "知识库已经是最新版本 (${latest_version})，无需更新"
    exit 0
fi

echo "发现新版本: ${latest_version}，开始下载更新..."

# 下载最新的知识库文件
curl -s -S -o ${TEMP_DB_FILE} ${update_url}
if [ $? -ne 0 ]; then
    echo "下载知识库文件失败！"
    exit 1
fi

# 验证下载的文件
sqlite3 ${TEMP_DB_FILE} "SELECT count(*) FROM sqlite_master;" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "下载的知识库文件验证失败，可能已损坏！"
    rm -f ${TEMP_DB_FILE}
    exit 1
fi

# 备份当前知识库
if [ -f "${DB_FILE}" ]; then
    backup_file="${BACKUP_DIR}/tcm_knowledge_$(date '+%Y%m%d').db"
    echo "备份当前知识库到 ${backup_file}"
    cp ${DB_FILE} ${backup_file}
fi

# 更新知识库
echo "更新知识库文件..."
mv ${TEMP_DB_FILE} ${DB_FILE}

# 更新版本信息
echo "更新版本信息..."
echo "{\"version\": \"${latest_version}\", \"update_time\": \"$(date '+%Y-%m-%d %H:%M:%S')\"}" > ${VERSION_FILE}

# 清理临时文件
rm -rf ${TEMP_DIR}

# 保留最近7天的备份，删除旧备份
find ${BACKUP_DIR} -name "tcm_knowledge_*.db" -type f -mtime +7 -delete

echo "==============================================="
echo "知识库更新完成 - 当前版本: ${latest_version}"
echo "==============================================="

exit 0 