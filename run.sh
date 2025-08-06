#!/bin/bash

# ================= 配置区 =================
APP_NAME="mecoo-datahub"
JAR_NAME="mecoo-datahub.jar"          # 需启动的JAR包名（必改）
JVM_OPTS="-Xms2g -Xmx4g"    # JVM参数
SPRING_OPTS="--spring.profiles.active=prod"                   # Spring Boot参数（非Spring可删除）
LOG_DIR="./logs"                 # 日志目录
MAX_LOG_DAYS=180                   # 日志保留天数

# ================= 核心逻辑 =================
# 初始化目录
init() {
  [ ! -d "$LOG_DIR" ] && mkdir -p "$LOG_DIR"
}

# 启动服务
start() {
  # 检查是否已运行
  if is_running; then
    echo "⚠️ $APP_NAME 服务已在运行 (PID: $(get_pid))"
    return 1
  fi

  # 日志文件名 (按日期)
  LOG_FILE="$LOG_DIR/app_$(date +'%Y%m%d_%H%M%S').log"

  echo "🚀 $APP_NAME 启动中  ..."
  nohup java $JVM_OPTS -jar $JAR_NAME $SPRING_OPTS > $LOG_FILE 2>&1 &

  sleep 2
  if is_running; then
    echo "✅ $APP_NAME 启动成功 (PID: $(get_pid), 日志: $LOG_FILE)"
  else
    echo "❌ $APP_NAME 启动失败！检查日志: $LOG_FILE"
  fi
}

# 停止服务
stop() {
  if ! is_running; then
    echo "⚠️ $APP_NAME 服务未运行"
    return
  fi

  PID=$(get_pid)
  echo "🛑 $APP_NAME 停止服务中 (PID: $PID)..."
  kill $PID

  local timeout=10
  while [ $timeout -gt 0 ] && is_running; do
    sleep 1
    timeout=$((timeout-1))
  done

  if is_running; then
    echo "❌ $APP_NAME 强制终止服务！"
    kill -9 $PID
  else
    echo "✅ $APP_NAME 服务已停止"
  fi
}

# 清理旧日志
clean_logs() {
  find "$LOG_DIR" -name "app_*.log" -mtime +$MAX_LOG_DAYS -exec rm -f {} \;
  echo "🗑️ 已清理$MAX_LOG_DAYS天前的日志"
}

# ================= 辅助函数 =================
is_running() {
  [ -n "$(get_pid)" ]
}

get_pid() {
  pgrep -f "java.*$JAR_NAME"
}

# ================= 主流程 =================
case "$1" in
  start)
    init
    start
    ;;
  stop)
    stop
    ;;
  restart)
    stop
    sleep 2
    start
    ;;
  status)
    if is_running; then
      echo "🟢 $APP_NAME 运行中 (PID: $(get_pid))"
    else
      echo "🔴 $APP_NAME 未运行"
    fi
    ;;
  clean)
    clean_logs
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|status|clean}"
    exit 1
esac
