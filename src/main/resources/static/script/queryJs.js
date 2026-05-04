/**
 * 
 */
  /* ========= 打开/关闭 ========= */
  function openmanualPanel(){
    $("#manualPanel").fadeIn(120);
    // 自动聚焦
    setTimeout(() => document.getElementById('queryInput')?.focus(), 0);
  }

  function closemanualPanel(){
    $("#manualPanel").fadeOut(120);
  }

  // ESC 关闭
  document.addEventListener('keydown', (e) => {
    if(e.key === 'Escape'){
      const p = document.getElementById('manualPanel');
      if(p && p.style.display !== 'none') closemanualPanel();
    }
  });

  /* ========= Spotlight mouse-follow（如果你 theme.css 里带了 glassSpotlight::after） ========= */
  (function initSpotlight(){
    const els = document.querySelectorAll('.glassSpotlight');
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

  /* ========= 查询逻辑 ========= */
  const statusDot = () => document.getElementById('statusDot');
  const statusText = () => document.getElementById('statusText');

  function setStatus(type, text){
    statusDot().className = 'dot ' + type;
    statusText().textContent = text;
  }

  function isNumericLike(s){
    const v = (s || '').trim();
    return v.length > 0 && /^[0-9]+$/.test(v);
  }

  function escapeHtml(str){
    return String(str ?? '')
      .replaceAll('&','&amp;')
      .replaceAll('<','&lt;')
      .replaceAll('>','&gt;')
      .replaceAll('"','&quot;')
      .replaceAll("'","&#39;");
  }

  function renderEmpty(msg){
    $("#context").html(`
      <tr class="emptyRow"><td colspan="8">${escapeHtml(msg || '暂无数据')}</td></tr>
    `);
  }

  function renderRows(data, mode){
    if(!Array.isArray(data) || data.length === 0){
      renderEmpty('未查询到结果');
      setStatus('ok', '完成：未查询到结果');
      return;
    }
    let html = '';
    for(let i=0;i<data.length;i++){
      const row = data[i] || {};
      const done = (row.done === -1) ? '历史状态' : (row.done === 1) ? '已做' : '未做';
      html += `
        <tr>
          <td>${escapeHtml(row.number)}</td>
          <td>${escapeHtml(row.name)}</td>
          <td>${escapeHtml(row.testItem)}</td>
          <td>${escapeHtml(row.company)}</td>
          <td>${escapeHtml(row.testMethod)}</td>
          <td>${escapeHtml(row.ifF)}</td>
          <td>${escapeHtml(row.result)}</td>
          <td>${escapeHtml(done)}</td>
        </tr>
      `;
    }
    $("#context").html(html);
    setStatus('ok', `完成：共 ${data.length} 条`);
  }

  function runQuery(){
    const input = document.getElementById('queryInput').value.trim();
    if(!input){
      setStatus('err', '请输入短号或样品名称');
      renderEmpty('请输入短号或样品名称');
      return;
    }

    const numeric = isNumericLike(input);
    const url = '/record/api/query-samples';
    const payload = { keyword: input };

    setStatus('loading', `查询中：${numeric ? '短号' : '名称'}`);
    renderEmpty('查询中…');

    $.post({
      url,
      data: payload,
      success: function(data){
        renderRows(data, 'all');
        makeRowsDraggableGeneric('#queryTable tbody tr', 'query');
      },
      error: function(){
        setStatus('err', '查询失败：接口异常或网络问题');
        renderEmpty('查询失败');
      }
    });
  }

  // 回车触发查询
  document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('queryInput');
    input?.addEventListener('keydown', (e) => {
      if(e.key === 'Enter'){
        e.preventDefault();
        runQuery();
      }
    });
  });
