<!-- 面试记录页背景插画：档案叠纸 -->
<template>
  <div class="doodle-history" aria-hidden="true">
    <svg viewBox="0 0 400 280" fill="none" xmlns="http://www.w3.org/2000/svg">
      <!-- ═══ 右下：叠起来的档案纸 ═══ -->
      <g transform="translate(260, 160)">
        <rect x="0" y="40" width="60" height="70" rx="2" fill="none" stroke="#1A1A1A" stroke-width="1.2" transform="rotate(-5, 30, 75)"/>
        <rect x="5" y="30" width="60" height="70" rx="2" fill="none" stroke="#1A1A1A" stroke-width="1.2" transform="rotate(2, 35, 65)"/>
        <rect x="3" y="20" width="60" height="70" rx="2" fill="none" stroke="#1A1A1A" stroke-width="1.3" transform="rotate(-1, 33, 55)"/>
        <!-- 最上面一张：机密章 -->
        <text x="10" y="45" font-size="8" fill="#1A1A1A" font-weight="700" transform="rotate(-1, 33, 55)">机密</text>
        <line x1="8" y1="52" x2="50" y2="52" stroke="#1A1A1A" stroke-width="0.7" transform="rotate(-1, 33, 55)"/>
        <line x1="8" y1="60" x2="40" y2="60" stroke="#1A1A1A" stroke-width="0.7" transform="rotate(-1, 33, 55)"/>
        <line x1="8" y1="68" x2="45" y2="68" stroke="#1A1A1A" stroke-width="0.7" transform="rotate(-1, 33, 55)"/>
      </g>

      <!-- ═══ 左上：放大镜 + 列表 ═══ -->
      <g transform="translate(20, 30)">
        <!-- 放大镜 -->
        <circle cx="22" cy="22" r="14" stroke="#1A1A1A" stroke-width="1.5"/>
        <line x1="32" y1="32" x2="44" y2="44" stroke="#1A1A1A" stroke-width="1.8" stroke-linecap="round"/>
        <!-- 列表项 -->
        <g transform="translate(0, 65)">
          <circle cx="4" cy="4" r="2" fill="#1A1A1A" opacity="0.7"/>
          <line x1="10" y1="4" x2="36" y2="4" stroke="#1A1A1A" stroke-width="0.9" stroke-linecap="round"/>
        </g>
        <g transform="translate(0, 78)">
          <circle cx="4" cy="4" r="2" fill="#1A1A1A" opacity="0.5"/>
          <line x1="10" y1="4" x2="28" y2="4" stroke="#1A1A1A" stroke-width="0.9" stroke-linecap="round"/>
        </g>
        <g transform="translate(0, 91)">
          <circle cx="4" cy="4" r="2" fill="#1A1A1A" opacity="0.3"/>
          <line x1="10" y1="4" x2="32" y2="4" stroke="#1A1A1A" stroke-width="0.9" stroke-linecap="round"/>
        </g>
      </g>

      <!-- ═══ 中上：对号标记 ═══ -->
      <g transform="translate(170, 30)">
        <circle cx="16" cy="16" r="14" stroke="#1A1A1A" stroke-width="1.2" stroke-dasharray="4 3"/>
        <path d="M9 17 L14 22 L23 12" stroke="#1A1A1A" stroke-width="1.5" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      </g>

      <!-- ═══ 中下：小人翻档案 ═══ -->
      <g transform="translate(140, 200)">
        <circle cx="16" cy="8" r="6" stroke="#1A1A1A" stroke-width="1.5"/>
        <circle cx="14" cy="6.5" r="1" fill="#1A1A1A"/>
        <circle cx="18" cy="6" r="0.8" fill="#1A1A1A"/>
        <line x1="16" y1="14" x2="16" y2="30" stroke="#1A1A1A" stroke-width="1.5" stroke-linecap="round"/>
        <line x1="16" y1="30" x2="10" y2="42" stroke="#1A1A1A" stroke-width="1.3" stroke-linecap="round"/>
        <line x1="16" y1="30" x2="24" y2="42" stroke="#1A1A1A" stroke-width="1.3" stroke-linecap="round"/>
        <!-- 手拿纸 -->
        <line x1="16" y1="20" x2="26" y2="16" stroke="#1A1A1A" stroke-width="1.3" stroke-linecap="round"/>
        <rect x="24" y="8" width="14" height="18" rx="1" fill="none" stroke="#1A1A1A" stroke-width="1"/>
        <line x1="27" y1="13" x2="35" y2="13" stroke="#1A1A1A" stroke-width="0.7"/>
        <line x1="27" y1="17" x2="33" y2="17" stroke="#1A1A1A" stroke-width="0.7"/>
        <line x1="27" y1="21" x2="34" y2="21" stroke="#1A1A1A" stroke-width="0.7"/>
      </g>
    </svg>
  </div>
</template>

<style scoped>
.doodle-history {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}
.doodle-history svg {
  width: 100%;
  height: 100%;
  opacity: 0.06;
}
</style>
