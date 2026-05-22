const state = {
  currentSector: "semiconductor",
  lastRun: null,
  nextRun: null,
  runCount: 0,
  resizeTimer: null,
};

const sectors = {
  semiconductor: {
    name: "半导体板块",
    eyebrow: "A 股 / 港股 / 美股映射",
    score: 82,
    trend: "上行确认",
    summary:
      "半导体近日呈现资金回补与事件催化共振，先进封装、存储、设备材料相对强势。短期需要观察成交额是否持续放大，以及海外科技股波动对估值锚的扰动。",
    values: [100, 102, 101, 104, 108, 110, 113, 111, 116, 119, 122, 121],
    flows: [
      ["主力资金", 18.6, 72],
      ["北向/外资映射", 7.4, 58],
      ["ETF 申购", 5.8, 64],
      ["融资余额变化", 3.1, 46],
    ],
    events: [
      ["利好", "先进封装订单预期上修", "龙头公司资本开支指引改善，设备、材料、封测链条获得估值修复窗口。"],
      ["利好", "国产替代政策延续", "供应链安全主题仍在，资金更偏向确定性高、业绩兑现更快的细分方向。"],
      ["利空", "海外科技股波动加剧", "若纳指与费城半导体指数回撤，短线高估值品种可能跟随降温。"],
      ["观察", "库存周期拐点验证", "存储价格改善仍需等待下游订单持续确认，避免只交易预期。"],
    ],
  },
  gold: {
    name: "黄金链",
    eyebrow: "贵金属 / 矿业 / 避险资产",
    score: 76,
    trend: "高位震荡",
    summary:
      "黄金维持高位波动，实际利率、美元指数和地缘风险共同驱动。矿企弹性强于金价本身，但若美元反弹或获利盘集中释放，短期波动会明显放大。",
    values: [100, 101, 103, 106, 105, 107, 110, 108, 109, 112, 111, 113],
    flows: [
      ["黄金 ETF", 9.8, 68],
      ["矿业股主力", 6.2, 54],
      ["期货持仓", 4.6, 49],
      ["避险配置", 8.1, 62],
    ],
    events: [
      ["利好", "避险需求仍有支撑", "宏观不确定性提高黄金配置价值，黄金 ETF 与矿业股同步受益。"],
      ["利好", "央行购金预期", "长期配置需求强化金价底部，但短线节奏仍受美元和利率影响。"],
      ["利空", "美元阶段性反弹", "美元走强会压制无息资产估值，黄金链高弹性个股可能先于金价调整。"],
      ["观察", "实际利率变化", "若实际利率继续下行，金价趋势更容易向上突破。"],
    ],
  },
  global: {
    name: "全球波动",
    eyebrow: "美股 / 汇率 / 商品 / VIX",
    score: 69,
    trend: "风险抬升",
    summary:
      "全球波动处在中等偏高区间，权益、商品和汇率之间的相关性上升。配置上更适合保留现金弹性，关注美元指数、VIX 与原油波动对风险资产的传导。",
    values: [100, 99, 101, 98, 103, 102, 106, 105, 107, 104, 108, 110],
    flows: [
      ["美股 ETF", -5.2, 42],
      ["美元资产", 10.6, 71],
      ["商品基金", 3.8, 51],
      ["避险债券", 6.9, 59],
    ],
    events: [
      ["利好", "风险对冲需求上升", "波动策略、美元资产和高等级债券获得阶段性配置需求。"],
      ["利空", "权益估值敏感度提高", "高估值成长资产对利率与盈利预期变化更敏感。"],
      ["利空", "商品价格波动扩大", "原油和有色波动会向通胀预期传导，影响全球风险偏好。"],
      ["观察", "VIX 是否持续上行", "若波动率站稳高位，市场可能从轮动行情切换为防御行情。"],
    ],
  },
};

const dashboardCards = [
  {
    id: "semiconductor",
    title: "板块资金流向",
    value: "+41.7 亿",
    tone: "positive",
    desc: "半导体、算力硬件与创新药获得主要净流入，周期资源出现分化。",
  },
  {
    id: "gold",
    title: "黄金波动",
    value: "2.8%",
    tone: "neutral",
    desc: "黄金维持高位宽幅震荡，避险买盘和美元反弹形成拉扯。",
  },
  {
    id: "global",
    title: "全球波动",
    value: "偏高",
    tone: "negative",
    desc: "VIX、美元指数与原油波动同步抬升，全球资产相关性增强。",
  },
];

const flowRows = [
  ["半导体", "+18.6 亿", "先进封装、设备材料领涨", "positive"],
  ["黄金链", "+9.8 亿", "ETF 与矿业股同步回补", "positive"],
  ["新能源", "-7.1 亿", "价格竞争压制风险偏好", "negative"],
  ["消费", "+3.4 亿", "防御配置小幅修复", "neutral"],
];

const signals = [
  ["资金集中度提高", "净流入集中在科技与避险两端，说明市场仍在寻找确定性。"],
  ["波动率不低", "黄金与全球风险指标同时走强，短线不宜忽视回撤管理。"],
  ["事件催化有效", "政策、订单、海外指数表现对板块轮动的解释力上升。"],
];

const dom = {
  pageTitle: document.querySelector("#pageTitle"),
  lastUpdated: document.querySelector("#lastUpdated"),
  nextRunText: document.querySelector("#nextRunText"),
  hourProgress: document.querySelector("#hourProgress"),
  dashboardView: document.querySelector("#dashboardView"),
  sectorView: document.querySelector("#sectorView"),
  dashboardCards: document.querySelector("#dashboardCards"),
  flowTable: document.querySelector("#flowTable"),
  signalList: document.querySelector("#signalList"),
  sectorName: document.querySelector("#sectorName"),
  sectorEyebrow: document.querySelector("#sectorEyebrow"),
  sectorSummaryText: document.querySelector("#sectorSummaryText"),
  sectorScore: document.querySelector("#sectorScore"),
  trendTag: document.querySelector("#trendTag"),
  sectorFlow: document.querySelector("#sectorFlow"),
  eventList: document.querySelector("#eventList"),
  eventBalance: document.querySelector("#eventBalance"),
  marketCanvas: document.querySelector("#marketCanvas"),
  trendCanvas: document.querySelector("#trendCanvas"),
};

function moneyClass(value) {
  return value.includes("-") ? "negative" : value.includes("+") ? "positive" : "neutral";
}

function runAnalysis() {
  const now = new Date();
  state.runCount += 1;
  state.lastRun = now;
  state.nextRun = new Date(now.getTime() + 60 * 60 * 1000);
  dom.lastUpdated.textContent = `已分析 ${now.toLocaleTimeString("zh-CN", { hour12: false })}`;
  drawMarketCanvas();
  renderDashboard();
  renderSector(state.currentSector);
}

function renderDashboard() {
  dom.dashboardCards.innerHTML = dashboardCards
    .map(
      (card) => `
        <article class="metric-card" data-sector="${card.id}" tabindex="0">
          <div class="metric-top">
            <strong>${card.title}</strong>
            <span class="tag ${card.tone}">${sectors[card.id].trend}</span>
          </div>
          <div class="metric-value ${card.tone}">${card.value}</div>
          <p class="metric-desc">${card.desc}</p>
        </article>
      `,
    )
    .join("");

  dom.flowTable.innerHTML = flowRows
    .map(
      ([name, amount, sub, tone]) => `
        <div class="flow-row">
          <div>
            <span class="flow-name">${name}</span>
            <span class="flow-sub">${sub}</span>
          </div>
          <span class="flow-amount ${tone}">${amount}</span>
        </div>
      `,
    )
    .join("");

  dom.signalList.innerHTML = signals
    .map(
      ([title, body]) => `
        <article class="signal-item">
          <strong>${title}</strong>
          <p>${body}</p>
        </article>
      `,
    )
    .join("");

  document.querySelectorAll(".metric-card").forEach((card) => {
    card.addEventListener("click", () => openSector(card.dataset.sector));
    card.addEventListener("keydown", (event) => {
      if (event.key === "Enter") openSector(card.dataset.sector);
    });
  });
}

function renderSector(sectorId) {
  state.currentSector = sectorId;
  const data = sectors[sectorId];
  dom.sectorName.textContent = data.name;
  dom.sectorEyebrow.textContent = data.eyebrow;
  dom.sectorSummaryText.textContent = data.summary;
  dom.sectorScore.textContent = data.score;
  dom.trendTag.textContent = data.trend;
  dom.trendTag.className = `tag ${data.score >= 78 ? "positive" : data.score >= 70 ? "neutral" : "negative"}`;

  dom.sectorFlow.innerHTML = data.flows
    .map(
      ([name, amount, width]) => `
        <div class="flow-meter">
          <div class="meter-head">
            <span>${name}</span>
            <span class="${amount >= 0 ? "positive" : "negative"}">${amount >= 0 ? "+" : ""}${amount} 亿</span>
          </div>
          <div class="meter-track"><div class="meter-bar" style="width:${width}%"></div></div>
        </div>
      `,
    )
    .join("");

  const good = data.events.filter(([impact]) => impact === "利好").length;
  const bad = data.events.filter(([impact]) => impact === "利空").length;
  dom.eventBalance.textContent = good > bad ? "利好多于利空" : good < bad ? "利空需警惕" : "多空均衡";
  dom.eventBalance.className = `tag ${good > bad ? "positive" : good < bad ? "negative" : "neutral"}`;
  dom.eventList.innerHTML = data.events
    .map(([impact, title, body]) => {
      const impactClass = impact === "利好" ? "good" : impact === "利空" ? "bad" : "watch";
      return `
        <article class="event-item">
          <div class="event-meta">
            <span class="impact ${impactClass}">${impact}</span>
            <strong>${title}</strong>
          </div>
          <p>${body}</p>
        </article>
      `;
    })
    .join("");

  document.querySelectorAll(".segment").forEach((button) => {
    button.classList.toggle("active", button.dataset.sector === sectorId);
  });
  if (dom.sectorView.classList.contains("active-view")) {
    document.querySelectorAll(".nav-item").forEach((button) => {
      button.classList.toggle("active", button.dataset.sector === sectorId);
    });
  }
  drawTrendCanvas(data.values, sectorId);
}

function openSector(sectorId) {
  dom.dashboardView.classList.remove("active-view");
  dom.sectorView.classList.add("active-view");
  dom.pageTitle.textContent = `${sectors[sectorId].name}趋势与事件分析`;
  renderSector(sectorId);
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function openDashboard() {
  dom.sectorView.classList.remove("active-view");
  dom.dashboardView.classList.add("active-view");
  dom.pageTitle.textContent = "跨市场资金与波动监控";
  document.querySelectorAll(".nav-item").forEach((button) => {
    button.classList.toggle("active", button.dataset.view === "dashboard");
  });
  window.scrollTo({ top: 0, behavior: "smooth" });
}

function updateCountdown() {
  if (!state.nextRun || !state.lastRun) return;
  const now = new Date();
  const total = state.nextRun - state.lastRun;
  const remaining = Math.max(0, state.nextRun - now);
  const minutes = Math.floor(remaining / 60000);
  const seconds = Math.floor((remaining % 60000) / 1000);
  dom.nextRunText.textContent = `下次分析 ${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
  dom.hourProgress.style.width = `${Math.min(100, ((total - remaining) / total) * 100)}%`;
  if (remaining === 0) runAnalysis();
}

function prepareCanvas(canvas) {
  if (!canvas.dataset.baseWidth) {
    canvas.dataset.baseWidth = String(canvas.width);
    canvas.dataset.baseHeight = String(canvas.height);
  }

  const rect = canvas.getBoundingClientRect();
  const cssWidth = Math.max(1, Math.round(rect.width || canvas.clientWidth || canvas.width));
  const cssHeight = Math.max(1, Math.round(rect.height || canvas.clientHeight || canvas.height));
  const useCssSize = cssWidth < 560;
  const logicalWidth = useCssSize ? cssWidth : Number(canvas.dataset.baseWidth);
  const logicalHeight = useCssSize ? cssHeight : Number(canvas.dataset.baseHeight);
  const ratio = Math.min(window.devicePixelRatio || 1, 2);

  canvas.width = Math.round(logicalWidth * ratio);
  canvas.height = Math.round(logicalHeight * ratio);
  const ctx = canvas.getContext("2d");
  ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
  return { ctx, width: logicalWidth, height: logicalHeight, cssWidth };
}

function drawMarketCanvas() {
  const canvas = dom.marketCanvas;
  const { ctx, width, height, cssWidth } = prepareCanvas(canvas);
  ctx.clearRect(0, 0, width, height);
  ctx.fillStyle = "#111a21";
  ctx.fillRect(0, 0, width, height);

  if (cssWidth < 560) {
    const mobileGrid = [
      ["半导体", 0.82, "#24a17b"],
      ["黄金", 0.76, "#d6a63b"],
      ["美元", 0.68, "#5aa0d8"],
      ["原油", -0.44, "#d66a5f"],
      ["VIX", 0.58, "#7aa0b8"],
      ["创新药", 0.47, "#24a17b"],
      ["新能源", -0.36, "#d66a5f"],
      ["美债", 0.32, "#5aa0d8"],
      ["有色", 0.29, "#d6a63b"],
      ["消费", 0.18, "#5fb7bd"],
    ];
    const margin = 14;
    const gap = 8;
    const cols = 2;
    const rows = Math.ceil(mobileGrid.length / cols);
    const tileWidth = (width - margin * 2 - gap) / cols;
    const tileHeight = Math.max(38, (height - 96 - margin * 2 - gap * (rows - 1)) / rows);

    mobileGrid.forEach(([label, value, color], index) => {
      const col = index % cols;
      const row = Math.floor(index / cols);
      const x = margin + col * (tileWidth + gap);
      const y = margin + row * (tileHeight + gap);
      ctx.globalAlpha = 0.2 + Math.abs(value) * 0.48;
      ctx.fillStyle = color;
      ctx.fillRect(x, y, tileWidth, tileHeight);
      ctx.globalAlpha = 1;
      ctx.strokeStyle = "rgba(255,255,255,.18)";
      ctx.strokeRect(x, y, tileWidth, tileHeight);
      ctx.fillStyle = "#fff";
      ctx.font = "700 13px Microsoft YaHei, Arial";
      ctx.fillText(label, x + 10, y + 21);
      ctx.fillStyle = "rgba(255,255,255,.72)";
      ctx.font = "600 10px Microsoft YaHei, Arial";
      ctx.fillText(`${value > 0 ? "+" : ""}${Math.round(value * 100)} 风险分`, x + 10, y + 37);
    });
    return;
  }

  const grid = [
    ["半导体", 0.82, "#24a17b", 35, 34, 260, 118],
    ["黄金", 0.76, "#d6a63b", 315, 34, 210, 118],
    ["美元", 0.68, "#5aa0d8", 545, 34, 180, 118],
    ["原油", -0.44, "#d66a5f", 745, 34, 150, 118],
    ["VIX", 0.58, "#7aa0b8", 915, 34, 150, 118],
    ["创新药", 0.47, "#24a17b", 35, 172, 180, 92],
    ["新能源", -0.36, "#d66a5f", 235, 172, 160, 92],
    ["美债", 0.32, "#5aa0d8", 415, 172, 175, 92],
    ["有色", 0.29, "#d6a63b", 610, 172, 170, 92],
    ["消费", 0.18, "#5fb7bd", 800, 172, 265, 92],
  ];

  grid.forEach(([label, value, color, x, y, w, h]) => {
    ctx.globalAlpha = 0.17 + Math.abs(value) * 0.48;
    ctx.fillStyle = color;
    ctx.fillRect(x, y, w, h);
    ctx.globalAlpha = 1;
    ctx.strokeStyle = "rgba(255,255,255,.18)";
    ctx.strokeRect(x, y, w, h);
    ctx.fillStyle = "#fff";
    ctx.font = "700 20px Microsoft YaHei, Arial";
    ctx.fillText(label, x + 16, y + 36);
    ctx.fillStyle = "rgba(255,255,255,.72)";
    ctx.font = "600 14px Microsoft YaHei, Arial";
    ctx.fillText(`${value > 0 ? "+" : ""}${Math.round(value * 100)} 风险分`, x + 16, y + 64);
  });
}

function drawTrendCanvas(values, sectorId) {
  const canvas = dom.trendCanvas;
  const { ctx, width, height, cssWidth } = prepareCanvas(canvas);
  const pad = cssWidth < 480 ? 28 : 42;
  const max = Math.max(...values);
  const min = Math.min(...values);
  const color = sectorId === "gold" ? "#b27a19" : sectorId === "global" ? "#2563a9" : "#16865c";
  const lineWidth = cssWidth < 480 ? 3 : 4;

  ctx.clearRect(0, 0, width, height);
  ctx.fillStyle = "#ffffff";
  ctx.fillRect(0, 0, width, height);
  ctx.strokeStyle = "#dce4ed";
  ctx.lineWidth = 1;

  for (let i = 0; i < 5; i += 1) {
    const y = pad + ((height - pad * 2) / 4) * i;
    ctx.beginPath();
    ctx.moveTo(pad, y);
    ctx.lineTo(width - pad, y);
    ctx.stroke();
  }

  const points = values.map((value, index) => {
    const x = pad + ((width - pad * 2) / (values.length - 1)) * index;
    const y = height - pad - ((value - min) / (max - min || 1)) * (height - pad * 2);
    return [x, y];
  });

  const gradient = ctx.createLinearGradient(0, pad, 0, height - pad);
  gradient.addColorStop(0, `${color}55`);
  gradient.addColorStop(1, `${color}00`);

  ctx.beginPath();
  points.forEach(([x, y], index) => {
    if (index === 0) ctx.moveTo(x, y);
    else ctx.lineTo(x, y);
  });
  ctx.lineTo(width - pad, height - pad);
  ctx.lineTo(pad, height - pad);
  ctx.closePath();
  ctx.fillStyle = gradient;
  ctx.fill();

  ctx.beginPath();
  points.forEach(([x, y], index) => {
    if (index === 0) ctx.moveTo(x, y);
    else ctx.lineTo(x, y);
  });
  ctx.strokeStyle = color;
  ctx.lineWidth = lineWidth;
  ctx.stroke();

  points.forEach(([x, y], index) => {
    ctx.beginPath();
    ctx.fillStyle = index === points.length - 1 ? "#111a21" : color;
    ctx.arc(x, y, index === points.length - 1 ? 6 : 4, 0, Math.PI * 2);
    ctx.fill();
  });

  ctx.fillStyle = "#657384";
  ctx.font = `${cssWidth < 480 ? "11px" : "13px"} Microsoft YaHei, Arial`;
  ctx.fillText(cssWidth < 420 ? "近 12 周期" : "近 12 个交易周期", pad, height - 12);
  ctx.fillText(`区间 ${min} - ${max}`, Math.max(pad, width - (cssWidth < 480 ? 92 : 150)), height - 12);
}

document.querySelector("#refreshBtn").addEventListener("click", runAnalysis);
document.querySelector("#backBtn").addEventListener("click", openDashboard);

document.querySelectorAll(".nav-item").forEach((button) => {
  button.addEventListener("click", () => {
    if (button.dataset.view === "dashboard") openDashboard();
    else openSector(button.dataset.sector);
  });
});

document.querySelectorAll(".segment").forEach((button) => {
  button.addEventListener("click", () => openSector(button.dataset.sector));
});

window.addEventListener("resize", () => {
  clearTimeout(state.resizeTimer);
  state.resizeTimer = setTimeout(() => {
    drawMarketCanvas();
    drawTrendCanvas(sectors[state.currentSector].values, state.currentSector);
  }, 120);
});

runAnalysis();
setInterval(updateCountdown, 1000);
setInterval(runAnalysis, 60 * 60 * 1000);
