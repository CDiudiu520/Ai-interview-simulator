<!-- 创建面试页背景插画：笔在写字 -->
<template>
  <div class="doodle-create" aria-hidden="true">
    <svg viewBox="0 0 400 280" fill="none" xmlns="http://www.w3.org/2000/svg">
      <!-- ═══ 右上：一张表单纸 ═══ -->
      <g transform="translate(260, 30)">
        <rect x="0" y="0" width="70" height="90" rx="2" fill="none" stroke="#1A1A1A" stroke-width="1.3" transform="rotate(4, 35, 45)"/>
        <line x1="10" y1="16" x2="58" y2="16" stroke="#1A1A1A" stroke-width="0.9" transform="rotate(4, 35, 45)"/>
        <line x1="10" y1="26" x2="50" y2="26" stroke="#1A1A1A" stroke-width="0.9" transform="rotate(4, 35, 45)"/>
        <line x1="10" y1="36" x2="55" y2="36" stroke="#1A1A1A" stroke-width="0.9" transform="rotate(4, 35, 45)"/>
        <!-- 填空横线 -->
        <line x1="10" y1="50" x2="60" y2="50" stroke="#1A1A1A" stroke-width="0.7" stroke-dasharray="4 3" transform="rotate(4, 35, 45)"/>
        <line x1="10" y1="60" x2="60" y2="60" stroke="#1A1A1A" stroke-width="0.7" stroke-dasharray="4 3" transform="rotate(4, 35, 45)"/>
        <line x1="10" y1="70" x2="45" y2="70" stroke="#1A1A1A" stroke-width="0.7" stroke-dasharray="4 3" transform="rotate(4, 35, 45)"/>
        <!-- 勾选标记 -->
        <path d="M42 40 L45 43 L52 35" stroke="#1A1A1A" stroke-width="1.1" fill="none" stroke-linecap="round" stroke-linejoin="round" transform="rotate(4, 35, 45)"/>
      </g>

      <!-- ═══ 左下：一支笔在写字 ═══ -->
      <g transform="translate(30, 180)">
        <!-- 笔身 -->
        <line x1="0" y1="60" x2="18" y2="15" stroke="#1A1A1A" stroke-width="2" stroke-linecap="round"/>
        <!-- 笔帽（飞出去的） -->
        <path d="M20 10 Q28 2 35 5" stroke="#1A1A1A" stroke-width="1.3" fill="none" stroke-linecap="round"/>
        <circle cx="36" cy="6" r="2.5" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <!-- 线条（表示写过的地方） -->
        <path d="M0 60 Q30 55 60 58" stroke="#1A1A1A" stroke-width="1.2" fill="none" stroke-dasharray="8 4"/>
        <!-- 纸张 -->
        <rect x="-10" y="55" width="80" height="8" rx="1" fill="none" stroke="#1A1A1A" stroke-width="1" transform="rotate(-3, 30, 59)"/>
      </g>

      <!-- ═══ 中右：机器人手指向表单 ═══ -->
      <g transform="translate(290, 140)">
        <!-- 小机器手指向纸 -->
        <rect x="0" y="5" width="18" height="16" rx="4" stroke="#1A1A1A" stroke-width="1.5"/>
        <circle cx="6" cy="12" r="1.8" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <circle cx="6" cy="12" r="0.8" fill="#1A1A1A"/>
        <circle cx="12" cy="11.5" r="1.5" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <circle cx="12" cy="11.5" r="0.7" fill="#1A1A1A"/>
        <!-- 指向上方的手 -->
        <line x1="9" y1="5" x2="6" y2="-4" stroke="#1A1A1A" stroke-width="1.3" stroke-linecap="round"/>
        <circle cx="5.5" cy="-5.5" r="1.5" fill="none" stroke="#1A1A1A" stroke-width="1.1"/>
      </g>

      <!-- ═══ 顶部中间：对话气泡 ═══ -->
      <g transform="translate(140, 20)">
        <ellipse cx="40" cy="25" rx="30" ry="18" fill="none" stroke="#1A1A1A" stroke-width="1.2" stroke-dasharray="3 3"/>
        <text x="22" y="30" font-size="13" fill="#1A1A1A" font-family="var(--font-body)">📋</text>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.doodle-create {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.doodle-create svg {
  width: 100%;
  height: 100%;
  opacity: 0.06;
}
</style>
