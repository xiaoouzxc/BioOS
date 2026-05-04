/**
 * 追加：Spotlight 跟随 JS（如果你主面板已经有了，这段也可直接用）
 */
 (function initSpotlightForMain(){
    const els = document.querySelectorAll('#tableDiv, #buttonContainer');
    els.forEach(el => {
      el.addEventListener('mousemove', (e) => {
        const r = el.getBoundingClientRect();
        const x = ((e.clientX - r.left) / r.width) * 100;
        const y = ((e.clientY - r.top) / r.height) * 100;
        el.style.setProperty('--mx', x + '%');
        el.style.setProperty('--my', y + '%');
      });
    });
  })();