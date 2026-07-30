<!-- 面试对话页背景插画：对话框 + 机器人挥手 -->
<template>
  <div class="doodle-session" aria-hidden="true">
    <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg">
      <!-- ═══ 左上：对话气泡（AI） ═══ -->
      <g transform="translate(20, 20)">
        <rect x="0" y="0" width="80" height="30" rx="10" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <line x1="10" y1="9" x2="68" y2="9" stroke="#1A1A1A" stroke-width="0.8"/>
        <line x1="10" y1="16" x2="55" y2="16" stroke="#1A1A1A" stroke-width="0.8"/>
        <line x1="10" y1="23" x2="60" y2="23" stroke="#1A1A1A" stroke-width="0.8"/>
        <!-- 气泡小三角 -->
        <polygon points="12,30 16,36 20,30" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
      </g>

      <!-- ═══ 右下：对话气泡（用户） ═══ -->
      <g transform="translate(260, 220)">
        <rect x="0" y="0" width="72" height="26" rx="10" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <line x1="10" y1="8" x2="60" y2="8" stroke="#1A1A1A" stroke-width="0.8"/>
        <line x1="10" y1="15" x2="50" y2="15" stroke="#1A1A1A" stroke-width="0.8"/>
        <polygon points="60,26 64,32 68,26" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
      </g>

      <!-- ═══ 中间：小机器人挥手 ═══ -->
      <g transform="translate(170, 100)">
        <!-- 天线 -->
        <line x1="22" y1="10" x2="26" y2="2" stroke="#1A1A1A" stroke-width="1.5" stroke-linecap="round"/>
        <circle cx="27" cy="0" r="2" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <!-- 头 -->
        <rect x="10" y="10" width="26" height="22" rx="6" stroke="#1A1A1A" stroke-width="1.6"/>
        <!-- 笑脸 -->
        <circle cx="19" cy="20" r="2.5" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <circle cx="19" cy="20" r="1" fill="#1A1A1A"/>
        <circle cx="27" cy="19" r="2.2" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <circle cx="27" cy="19" r="0.9" fill="#1A1A1A"/>
        <path d="M18 26 Q23 30 28 26" stroke="#1A1A1A" stroke-width="1.2" fill="none" stroke-linecap="round"/>
        <!-- 身体 -->
        <rect x="8" y="32" width="30" height="26" rx="5" stroke="#1A1A1A" stroke-width="1.6"/>
        <!-- 挥手的手 -->
        <path d="M38 40 Q48 30 52 28" stroke="#1A1A1A" stroke-width="1.5" fill="none" stroke-linecap="round"/>
        <!-- 运动线 -->
        <path d="M53 22 Q55 18 53 14" stroke="#1A1A1A" stroke-width="0.8" fill="none" stroke-linecap="round"/>
      </g>

      <!-- ═══ 顶部右：三个点（思考中） ═══ -->
      <g transform="translate(320, 30)">
        <circle cx="0" cy="0" r="2.5" fill="#1A1A1A" opacity="0.5"/>
        <circle cx="10" cy="0" r="2" fill="#1A1A1A" opacity="0.35"/>
        <circle cx="18" cy="0" r="1.5" fill="#1A1A1A" opacity="0.2"/>
      </g>

      <!-- ═══ 左下：星星（评分） ═══ -->
      <g transform="translate(20, 230)">
        <polygon points="12,0 15,8 24,9 17,15 19,24 12,20 5,24 7,15 0,9 9,8"
          fill="none" stroke="#1A1A1A" stroke-width="1.2" stroke-linejoin="round"/>
        <polygon points="38,0 41,8 50,9 43,15 45,24 38,20 31,24 33,15 26,9 35,8"
          fill="none" stroke="#1A1A1A" stroke-width="1.2" stroke-linejoin="round" opacity="0.5"/>
        <polygon points="64,0 67,8 76,9 69,15 71,24 64,20 57,24 59,15 52,9 61,8"
          fill="none" stroke="#1A1A1A" stroke-width="1.2" stroke-linejoin="round" opacity="0.2"/>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.doodle-session {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.doodle-session svg {
  width: 100%;
  height: 100%;
  opacity: 0.06;
}
</style>
