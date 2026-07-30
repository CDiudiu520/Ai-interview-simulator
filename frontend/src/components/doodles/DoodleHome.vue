<!-- 首页背景插画：小人爬楼梯 -->
<template>
  <div class="doodle-home" aria-hidden="true">
    <svg viewBox="0 0 400 300" fill="none" xmlns="http://www.w3.org/2000/svg">
      <!-- ═══ 楼梯 ═══ -->
      <g transform="translate(60, 110)" stroke="#1A1A1A" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
        <line x1="0" y1="0" x2="280" y2="0"/>
        <line x1="0" y1="0" x2="0" y2="18"/>
        <line x1="0" y1="18" x2="50" y2="18"/>
        <line x1="50" y1="18" x2="50" y2="36"/>
        <line x1="50" y1="36" x2="100" y2="36"/>
        <line x1="100" y1="36" x2="100" y2="54"/>
        <line x1="100" y1="54" x2="150" y2="54"/>
        <line x1="150" y1="54" x2="150" y2="72"/>
        <line x1="150" y1="72" x2="200" y2="72"/>
        <line x1="200" y1="72" x2="200" y2="90"/>
        <line x1="200" y1="90" x2="250" y2="90"/>
        <line x1="250" y1="90" x2="250" y2="108"/>
        <line x1="250" y1="108" x2="280" y2="108"/>
      </g>

      <!-- 台阶标签 -->
      <text x="10" y="127" font-size="8" fill="#1A1A1A" font-family="var(--font-body)" opacity="0.8">第1面</text>
      <text x="60" y="145" font-size="8" fill="#1A1A1A" font-family="var(--font-body)" opacity="0.8">第2面</text>
      <text x="110" y="163" font-size="8" fill="#1A1A1A" font-family="var(--font-body)" opacity="0.8">第3面</text>

      <!-- ═══ 小人（爬到第4级） ═══ -->
      <g transform="translate(178, 142)">
        <!-- 头 -->
        <circle cx="12" cy="8" r="7" stroke="#1A1A1A" stroke-width="1.7" fill="none"/>
        <!-- 开心的表情 -->
        <circle cx="9.5" cy="6.5" r="1.2" fill="#1A1A1A"/>
        <circle cx="14.5" cy="6" r="1" fill="#1A1A1A"/>
        <path d="M8.5 10 Q12 13.5 15.5 10" stroke="#1A1A1A" stroke-width="1.3" fill="none" stroke-linecap="round"/>
        <!-- 身体 -->
        <line x1="12" y1="15" x2="12" y2="34" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 腿（走路中） -->
        <line x1="12" y1="34" x2="5" y2="48" stroke="#1A1A1A" stroke-width="1.6" stroke-linecap="round"/>
        <line x1="12" y1="34" x2="20" y2="46" stroke="#1A1A1A" stroke-width="1.6" stroke-linecap="round"/>
        <!-- 手臂（挥手） -->
        <line x1="12" y1="22" x2="4" y2="14" stroke="#1A1A1A" stroke-width="1.5" stroke-linecap="round"/>
        <line x1="12" y1="22" x2="22" y2="18" stroke="#1A1A1A" stroke-width="1.5" stroke-linecap="round"/>
      </g>

      <!-- ═══ 右上角：星星 ═══ -->
      <g transform="translate(290, 30)">
        <polygon points="15,0 19,11 31,11 21,18 25,30 15,23 5,30 9,18 -1,11 11,11"
          fill="none" stroke="#1A1A1A" stroke-width="1.5" stroke-linejoin="round"/>
      </g>

      <!-- ═══ 右下角：小奖杯 ═══ -->
      <g transform="translate(290, 210)">
        <rect x="2" y="20" width="14" height="6" rx="1" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <rect x="5" y="18" width="5" height="3" rx="0.5" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <!-- 奖杯耳朵 -->
        <path d="M2 10 Q2 4 6 3" stroke="#1A1A1A" stroke-width="1.3" fill="none" stroke-linecap="round"/>
        <path d="M16 10 Q16 4 12 3" stroke="#1A1A1A" stroke-width="1.3" fill="none" stroke-linecap="round"/>
        <ellipse cx="9" cy="10" rx="5" ry="6" fill="none" stroke="#1A1A1A" stroke-width="1.3"/>
        <!-- 小星星在杯里 -->
        <text x="6" y="14" font-size="7" fill="#1A1A1A">★</text>
      </g>

      <!-- ═══ 左下角：日历 ═══ -->
      <g transform="translate(20, 230)">
        <rect x="0" y="5" width="28" height="24" rx="2" fill="none" stroke="#1A1A1A" stroke-width="1.2" transform="rotate(-6, 14, 17)"/>
        <text x="6" y="21" font-size="11" font-weight="700" fill="#1A1A1A" font-family="var(--font-body)" transform="rotate(-6, 14, 17)">12</text>
        <!-- 撕掉的一角 -->
        <path d="M0 5 Q4 9 5 5" stroke="#1A1A1A" stroke-width="1" fill="none"/>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.doodle-home {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.doodle-home svg {
  width: 100%;
  height: 100%;
  opacity: 0.06;
}
</style>
