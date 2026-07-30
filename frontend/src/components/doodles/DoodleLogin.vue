<!-- 登录页背景插画：火柴人面试者 vs 机器人面试官 -->
<template>
  <div class="doodle-login" aria-hidden="true">
    <svg viewBox="0 0 400 280" fill="none" xmlns="http://www.w3.org/2000/svg">
      <!-- ═══ 左侧：火柴人面试者 ═══ -->
      <g transform="translate(20, 60)">
        <!-- 身体 -->
        <circle cx="32" cy="16" r="10" stroke="#1A1A1A" stroke-width="1.9" stroke-linecap="round"/>
        <!-- 紧张的表情：两个小点眼睛 + 弯嘴 -->
        <circle cx="29" cy="14" r="1.4" fill="#1A1A1A"/>
        <circle cx="36" cy="13.5" r="1.2" fill="#1A1A1A"/>
        <path d="M28 20 Q33 24 37 20" stroke="#1A1A1A" stroke-width="1.4" stroke-linecap="round"/>
        <!-- 身体 -->
        <line x1="32" y1="26" x2="32" y2="58" stroke="#1A1A1A" stroke-width="2" stroke-linecap="round"/>
        <!-- 手臂（一只手挠头） -->
        <line x1="32" y1="34" x2="18" y2="22" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 另一只手自然下垂 -->
        <line x1="32" y1="34" x2="48" y2="48" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 腿 -->
        <line x1="32" y1="58" x2="21" y2="82" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <line x1="32" y1="58" x2="43" y2="82" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 四滴汗 -->
        <path d="M13 12 Q14 8 15 12" stroke="#1A1A1A" stroke-width="1.2" fill="none" stroke-linecap="round"/>
        <path d="M9 16 Q10 12 11 16" stroke="#1A1A1A" stroke-width="1" fill="none" stroke-linecap="round"/>
      </g>

      <!-- ═══ 右侧：机器人面试官 ═══ -->
      <g transform="translate(300, 50)">
        <!-- 天线 -->
        <line x1="28" y1="8" x2="32" y2="0" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <circle cx="33" cy="-1" r="2.5" fill="none" stroke="#1A1A1A" stroke-width="1.5"/>
        <!-- 头 -->
        <rect x="14" y="8" width="28" height="22" rx="6" stroke="#1A1A1A" stroke-width="1.9"/>
        <!-- 眼睛：LED 式 -->
        <circle cx="23" cy="18" r="3.5" fill="none" stroke="#1A1A1A" stroke-width="1.6"/>
        <circle cx="23" cy="18" r="1.5" fill="#1A1A1A"/>
        <circle cx="33" cy="17" r="3" fill="none" stroke="#1A1A1A" stroke-width="1.6"/>
        <circle cx="33" cy="17" r="1.3" fill="#1A1A1A"/>
        <!-- 嘴：一条直线 -->
        <line x1="22" y1="26" x2="34" y2="26" stroke="#1A1A1A" stroke-width="1.4" stroke-linecap="round"/>
        <!-- 脖子 -->
        <line x1="28" y1="30" x2="28" y2="36" stroke="#1A1A1A" stroke-width="1.6"/>
        <!-- 身体 -->
        <rect x="12" y="36" width="32" height="28" rx="5" stroke="#1A1A1A" stroke-width="1.9"/>
        <!-- 身体面板上的小灯 -->
        <circle cx="22" cy="48" r="1.8" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <circle cx="28" cy="48" r="1.8" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <circle cx="34" cy="48" r="1.8" fill="none" stroke="#1A1A1A" stroke-width="1.2"/>
        <!-- 手臂：左手举打分牌 -->
        <line x1="12" y1="42" x2="-8" y2="32" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 打分牌 -->
        <rect x="-24" y="20" width="20" height="14" rx="2" stroke="#1A1A1A" stroke-width="1.6" fill="none"/>
        <text x="-17" y="32" font-size="9" font-family="var(--font-body)" fill="#1A1A1A" font-weight="700">85</text>
        <!-- 右手放在桌上 -->
        <line x1="44" y1="42" x2="58" y2="52" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 腿 -->
        <line x1="22" y1="64" x2="18" y2="84" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <line x1="34" y1="64" x2="39" y2="84" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 脚 -->
        <ellipse cx="16" cy="87" rx="5" ry="2.5" fill="none" stroke="#1A1A1A" stroke-width="1.5"/>
        <ellipse cx="41" cy="87" rx="5" ry="2.5" fill="none" stroke="#1A1A1A" stroke-width="1.5"/>
      </g>

      <!-- ═══ 中间：小细节 ═══ -->
      <!-- 桌子/讲台 -->
      <rect x="140" y="170" width="120" height="4" rx="1" fill="none" stroke="#1A1A1A" stroke-width="1.3" stroke-dasharray="6 4"/>
      <line x1="170" y1="174" x2="170" y2="194" stroke="#1A1A1A" stroke-width="1.2"/>
      <line x1="230" y1="174" x2="230" y2="194" stroke="#1A1A1A" stroke-width="1.2"/>

      <!-- 对话泡泡：小机器人图标 + 问号 -->
      <g transform="translate(170, 105)">
        <rect x="0" y="0" width="60" height="28" rx="8" stroke="#1A1A1A" stroke-width="1.3" stroke-dasharray="3 3"/>
        <text x="14" y="19" font-size="13" fill="#1A1A1A" font-family="var(--font-body)">🤖❓</text>
      </g>

      <!-- 左下角：便签纸涂鸦 -->
      <g transform="translate(8, 210)">
        <rect x="0" y="0" width="42" height="30" rx="2" fill="none" stroke="#1A1A1A" stroke-width="1.1" transform="rotate(-8, 21, 15)"/>
        <line x1="8" y1="10" x2="34" y2="9" stroke="#1A1A1A" stroke-width="0.8" transform="rotate(-8, 21, 15)"/>
        <line x1="8" y1="16" x2="28" y2="15" stroke="#1A1A1A" stroke-width="0.8" transform="rotate(-8, 21, 15)"/>
        <line x1="8" y1="22" x2="30" y2="21" stroke="#1A1A1A" stroke-width="0.8" transform="rotate(-8, 21, 15)"/>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.doodle-login {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.doodle-login svg {
  width: 100%;
  height: 100%;
  opacity: 0.07;
}
</style>
