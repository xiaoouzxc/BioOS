/**
 * 
 */
 // ====== Drawer Open/Close ======
  const drawer = document.getElementById('compareDrawer');
  const dropzone = document.getElementById('compareDropzone');
  const list = document.getElementById('compareList');
  const btnClose = document.getElementById('compareClose');
  const btnClear = document.getElementById('compareClear');

  // 你顶部“数据比对”按钮：给它加一个 id，然后这里绑定
  // 例如：<button id="openCompare" class="navBtn">数据比对</button>
  const openBtn = document.getElementById('openCompare');

  function openDrawer(){
    drawer.classList.add('isOpen');
    drawer.setAttribute('aria-hidden', 'false');
  }
  function closeDrawer(){
    drawer.classList.remove('isOpen');
    drawer.setAttribute('aria-hidden', 'true');
    dropzone.classList.remove('isOver','isError');
  }
  function toggleDrawer(){
    drawer.classList.contains('isOpen') ? closeDrawer() : openDrawer();
  }

  if(openBtn){
    openBtn.addEventListener('click', openDrawer);
  } else {
    // 如果你不想改按钮id，可以手动把点击入口绑到已有按钮：
    // document.querySelector('你的数据比对按钮选择器')?.addEventListener('click', openDrawer);
    console.warn('未找到 #openCompare，请给“数据比对”按钮加 id="openCompare"');
  }

  btnClose?.addEventListener('click', closeDrawer);
  btnClear?.addEventListener('click', () => { list.innerHTML = ''; });

  document.addEventListener('keydown', (e) => {
    if(e.key === 'Escape' && drawer.classList.contains('isOpen')){
      closeDrawer();
    }
  });

  // ====== Drag from mainTable ======
  function extractRowText(tr){
    const tds = Array.from(tr.querySelectorAll('td'));
    // 取你最关心的字段（可按你业务改顺序）
    // 当前主表列：短号、结果、复测、名称、抬头、单位/限值、项目、方法
    const values = tds.map(td => td.innerText.trim().replace(/\s+/g,' '));
    return {
      // 给一个“标签”更像企业系统
      tag: values[0] || 'Row',
      text: values.join(' | ')
    };
  }
  function extractCompareKeys(tr, fromTable){
  const tds = Array.from(tr.querySelectorAll('td')).map(td => td.innerText.trim().replace(/\s+/g,' '));

  // fromTable: 'main' 或 'query'
  if(fromTable === 'main'){
    return {
      simpleNam: tds[3] || '',
      company:   tds[4] || '',
      testItem:  tds[6] || ''
    };
  }else{
    return {
      simpleNam: tds[1] || '',
      company:   tds[3] || '',     // queryTable 没有报告抬头
      testItem:  tds[2] || ''
    };
  }
}
//主表格，查询表格抓取
  function makeRowsDraggableGeneric(selector, fromTable){
  const rows = document.querySelectorAll(selector);
  rows.forEach(tr => {
    tr.setAttribute('draggable','true');

    tr.addEventListener('dragstart', (e) => {
      openDrawer();
      tr.classList.add('isDragSource');

      const payload = extractRowText(tr);
      payload.keys = extractCompareKeys(tr, fromTable);

      e.dataTransfer.effectAllowed = 'copy';
      e.dataTransfer.setData('text/plain', JSON.stringify(payload));
    });

    tr.addEventListener('dragend', () => {
      tr.classList.remove('isDragSource');
      dropzone.classList.remove('isOver','isError');
    });
  });
}

  // 如果你的表格数据是 AJAX 动态渲染，需要在渲染后调用 makeRowsDraggable()
  // 这里先页面加载时跑一次
  // 页面加载时
document.addEventListener('DOMContentLoaded', () => {
  makeRowsDraggableGeneric('#mainTable tbody tr', 'main');
  makeRowsDraggableGeneric('#queryTable tbody tr', 'query');
});

  // ====== Drop into Drawer ======
  // 必须阻止默认，drop 才会触发
  dropzone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropzone.classList.add('isOver');
    dropzone.classList.remove('isError');
    e.dataTransfer.dropEffect = 'copy';
  });

  dropzone.addEventListener('dragleave', () => {
    dropzone.classList.remove('isOver');
  });

  dropzone.addEventListener('drop', (e) => {
  e.preventDefault();
  dropzone.classList.remove('isOver');

  try{
    const raw = e.dataTransfer.getData('text/plain');
    const payload = JSON.parse(raw);

    // 1) 顶部显示抓取内容（你原来那段保留）
    const item = document.createElement('div');
    item.className = 'compareItem';
    item.innerHTML = `
      <div class="compareTag">${escapeHtml(payload.tag)}</div>
      <div class="compareText">${escapeHtml(payload.text)}</div>
    `;
    list.prepend(item);

    // 2) 拿三项 keys
    const keys = payload.keys || {};
    const simpleNam = (keys.simpleNam || '').trim();
    const company   = (keys.company || '').trim();
    const testItem  = (keys.testItem || '').trim();

    // 基础校验
    if(!simpleNam || !testItem){
      document.getElementById('compareMeta').innerHTML =
        `<div style="color:#ffb4c0;font-weight:900;">缺少比对参数：样品名称或检测项目为空</div>`;
      return;
    }

    // 3) 显示 meta
    renderCompareMeta({ simpleNam, company, testItem });

    // 4) loading
    renderCompareLoading();

    // 5) 组装 table_name（你必须按你真实规则改）
    // 如果你的 select 是 2026/01/07 -> test20260107
    const dateText = (document.getElementById('chuselectDate')?.value || '').trim();
    const table_name = dateText;
    //toTableNameFromDateText(dateText); // 下面我给这个函数

    // 6) 调后端接口
	queryCompareByParams({
	  table_name,
	  simpleNam,
	  company,
	  testItem
	});

  }catch(err){
    console.error(err);
    dropzone.classList.add('isError');
  }
});

  function escapeHtml(str){
    return String(str ?? '')
      .replaceAll('&','&amp;')
      .replaceAll('<','&lt;')
      .replaceAll('>','&gt;')
      .replaceAll('"','&quot;')
      .replaceAll("'","&#39;");
  }
  
  function renderCompareMeta({ table_name, simpleNam, company, testItem }){
    const el = document.getElementById('compareMeta');
    el.innerHTML = `
      <div class="compareMeta">
        ${renderEditableMetaChip('simpleNam', '样品名称', simpleNam)}
        ${renderEditableMetaChip('company', '报告抬头', company || '')}
        ${renderEditableMetaChip('testItem', '检测项目', testItem)}
      </div>
    `;

    bindCompareMetaEditEvents();
  }

  function renderEditableMetaChip(field, label, value){
    return `
      <span class="metaChip editableMetaChip" data-field="${field}">
        <strong>${label}：</strong>
        <span class="metaChipText">${escapeHtml(value || '（空）')}</span>
        <input class="metaChipInput" value="${escapeHtml(value || '')}" style="display:none;">
      </span>
    `;
  }

  function bindCompareMetaEditEvents(){
    document.querySelectorAll('#compareMeta .editableMetaChip').forEach(chip => {
      const text = chip.querySelector('.metaChipText');
      const input = chip.querySelector('.metaChipInput');

      text.addEventListener('click', () => {
        text.style.display = 'none';
        input.style.display = 'inline-block';
        input.focus();
        input.select();
      });

      input.addEventListener('keydown', e => {
        if(e.key === 'Enter'){
          e.preventDefault();
          input.blur();
        }
        if(e.key === 'Escape'){
          input.value = currentCompareParams?.[chip.dataset.field] || '';
          input.blur();
        }
      });

      input.addEventListener('blur', () => {
        const field = chip.dataset.field;
        const newValue = input.value.trim();
        const oldValue = currentCompareParams?.[field] || '';

        text.style.display = '';
        input.style.display = 'none';

        if(newValue === oldValue){
          text.innerHTML = escapeHtml(newValue || '（空）');
          return;
        }

        const nextParams = {
          ...currentCompareParams,
          [field]: newValue
        };

        if(!nextParams.simpleNam || !nextParams.testItem){
          alert('样品名称和检测项目不能为空');
          text.innerHTML = escapeHtml(oldValue || '（空）');
          input.value = oldValue;
          return;
        }

        queryCompareByParams(nextParams);
      });
    });
  }

function renderCompareLoading(){
  document.getElementById('compareTableWrap').innerHTML = `
    <div style="padding:12px;color:rgba(234,240,255,.75);font-weight:900;">
      查询比对数据中…
    </div>
  `;
}

function renderCompareTable(data){
  // 兼容你后端“未找到”返回
  if(!Array.isArray(data) || data.length === 0 ||
     (data.length === 1 && (data[0]?.name === '未找到' || data[0]?.number === '未找到'))){
    document.getElementById('compareTableWrap').innerHTML = `
      <div style="padding:12px;color:rgba(234,240,255,.75);font-weight:900;">
        未查询到可比对数据
      </div>
    `;
    return;
  }

  let rows = '';
  for(const r of data){
    rows += `
      <tr>
        <td title="${escapeHtml(r.number)}">${escapeHtml(r.number)}</td>
        <td title="${escapeHtml(r.name)}">${escapeHtml(r.name)}</td>
        <td title="${escapeHtml(r.company)}">${escapeHtml(r.company)}</td>
        <td title="${escapeHtml(r.testItem)}">${escapeHtml(r.testItem)}</td>
        <td title="${escapeHtml(r.result)}">${escapeHtml(r.result)}</td>
        <td title="${escapeHtml(r.ifF)}">${escapeHtml(r.ifF)}</td>
      </tr>
    `;
  }

  document.getElementById('compareTableWrap').innerHTML = `
    <div class="compareTableWrap">
      <table class="compareTable">
        <thead>
          <tr>
            <th style="width:120px;">短号</th>
            <th style="width:220px;">名称</th>
            <th style="width:240px;">报告抬头</th>
            <th style="width:160px;">检测项目</th>
            <th style="width:140px;">结果</th>
            <th>复测信息</th>
          </tr>
        </thead>
        <tbody>${rows}</tbody>
      </table>
    </div>
  `;
}

// 2026/01/07 或 2026-01-07 -> test20260107
function toTableNameFromDateText(v){
  const d = (v || '').replaceAll('/','').replaceAll('-','').trim();
  if(!/^\d{8}$/.test(d)) return '';   // 兜底：拿不到就返回空
  return 'test' + d;                  // 按你的表命名规则改
}

  // ====== 重要：如果你刷新/筛选/翻页会重新渲染 tbody，
  // 需要在渲染完成后再调用 makeRowsDraggable()
  // 你可以在你现有“渲染表格函数”最后加一行：
  // makeRowsDraggable();
  
  
  let currentCompareParams = null;

  function queryCompareByParams(params){
    currentCompareParams = { ...params };

    renderCompareMeta(currentCompareParams);
    renderCompareLoading();

    $.post({
      url: '/ajax/QuerySimpletoComparefromYeartable',
      data: currentCompareParams,
      success: function(data){
        renderCompareTable(data);
      },
      error: function(){
        document.getElementById('compareTableWrap').innerHTML =
          `<div style="padding:12px;color:#ffb4c0;font-weight:900;">查询失败：接口异常</div>`;
      }
    });
  }