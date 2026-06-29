// 流量进度条渲染：以总流量为基准，按剩余百分比着色并设置进度宽度
document.addEventListener('DOMContentLoaded', function () {
    initTrafficProgress('#linesTableBody');
    // 新增：初始化连接占用进度条
    initConnectionProgress('#linesTableBody');
});

function initTrafficProgress(tbodySelector) {
    const cells = document.querySelectorAll(`${tbodySelector} td.traffic-cell`);
    cells.forEach(cell => {
        const totalStr = (cell.dataset.total || '').trim();
        const remainStr = (cell.dataset.remain || '').trim();

        const total = parseTrafficToTB(totalStr);
        const remain = parseTrafficToTB(remainStr);

        const percent = total > 0 ? clamp((remain / total) * 100, 0, 100) : 0;

        const fill = cell.querySelector('.traffic-progress__fill');
        if (!fill) return;

        // 阈值：>60% 绿色；30%~60% 黄色；<30% 红色
        let colorClass = 'is-red';
        if (percent > 60) {
            colorClass = 'is-green';
        } else if (percent > 30) {
            colorClass = 'is-yellow';
        } // 30%及以下为红色

        fill.style.width = `${percent.toFixed(0)}%`;
        fill.classList.remove('is-green', 'is-yellow', 'is-red');
        fill.classList.add(colorClass);
    });
}

// 新增：连接占用进度条渲染（以 current/max 百分比为基准）
function initConnectionProgress(tbodySelector) {
    const cells = document.querySelectorAll(`${tbodySelector} td.conn-cell`);
    cells.forEach(cell => {
        const current = parseFloat((cell.dataset.current || '0'));
        const max = parseFloat((cell.dataset.max || '0'));
        const percent = max > 0 ? clamp((current / max) * 100, 0, 100) : 0;

        const fill = cell.querySelector('.traffic-progress__fill');
        if (!fill) return;

        // 阈值：<=60% 绿色；60%~80% 黄色；>=80% 红色
        let colorClass = 'is-green';
        if (percent > 60 && percent < 80) {
            colorClass = 'is-yellow';
        } else if (percent >= 80) {
            colorClass = 'is-red';
        } else {
            colorClass = 'is-green';
        }

        fill.style.width = `${percent.toFixed(0)}%`;
        fill.classList.remove('is-green', 'is-yellow', 'is-red');
        fill.classList.add(colorClass);
    });
}

function parseTrafficToTB(str) {
    if (!str || str === '-' || str === '--') return 0;
    const s = String(str).toUpperCase().trim();
    const match = s.match(/([\d.]+)/);
    if (!match) return 0;
    const num = parseFloat(match[1]);
    if (isNaN(num)) return 0;

    // 统一转换到 GB
    if (s.includes('PB')) return num * (1024 * 1024);
    if (s.includes('TB')) return num * 1024;
    if (s.includes('GB')) return num;
    if (s.includes('MB')) return num / 1024;
    // 无单位时按 GB 处理
    return num;
}

function clamp(val, min, max) {
    return Math.max(min, Math.min(max, val));
}