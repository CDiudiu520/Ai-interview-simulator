<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-body);
  background: var(--paper-bg);
  color: var(--ink-primary);
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>
