// 分销商模块：搜索、筛选、添加、查看、编辑、删除、模态框控制等（导出功能已迁移到 common.js）
window.menu = 'dealer';

(function initDistributorsPage() {
  document.addEventListener('DOMContentLoaded', () => {
    searchDistributors();
    updateOverviewMetrics();
  });
})();


async function updateOverviewMetrics() {
  const response = await fetch(`/system/distributors/stats`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
  });
  const data = await response.json();

  if(data.code !== 0){
    toast(data.msg, 'error');
    return;
  }

  let total = data.data.stats.total | 0;
  let active = data.data.stats.active | 0;
  let newThisMonth = data.data.stats.newThisMonth | 0;

  const setMetric = (selector, value) => {
    const el = document.querySelector(selector);
    if (el) el.textContent = String(value);
  };

  // 对齐 HTML 中卡片的顺序和含义
  setMetric('#card-total-distributors .overview-metric', total); // 第一名人数
  setMetric('#card-active-distributors .overview-metric', active); // 第二名人数
  setMetric('#card-new-this-month .overview-metric', newThisMonth); // 无效人数

  const levelStatsContainer = document.getElementById("levelStatsContainer");
  const levelStats = data.data.levels;

  if(levelStatsContainer && levelStats){
    let html = '';
    levelStats.forEach(stats => {
      if(stats.level === 'diamond'){
        html += ` <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                    <span>钻石级</span>
                    <span style="color: var(--accent-color); font-weight: bold;">${stats.count}家</span>
                </div>`
      }else if(stats.level === 'gold'){
        html += ` <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                    <span>黄金级</span>
                    <span style="color: var(--success-color); font-weight: bold;">${stats.count}家</span>
                </div>`
      }else if(stats.level === 'silver'){
        html += ` <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
                    <span>白银级</span>
                    <span style="color: var(--warning-color); font-weight: bold;">${stats.count}家</span>
                </div>`
      }else if(stats.level === 'normal'){
        html += ` <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span>普通级</span>
                    <span style="color: var(--text-secondary);">${stats.count}家</span>
                </div>`
      }
    });
    levelStatsContainer.innerHTML = html;
  }

  const chartDom = document.getElementById('salesTrendChart');
  const trend = data.data.trend;
  if(chartDom){
    var myChart = echarts.init(chartDom);

    var months = [...new Set(trend.map(d => d.month))].sort();
    // 2️⃣ 获取所有区域
    var regions = [...new Set(trend.map(d => DISTRIBUTOR_REGION[d.region]))];

    // 3️⃣ 为每个区域生成 series
    var series = regions.map(region => {
      return {
        name: region,
        type: 'line',
        smooth: true,
        data: months.map(month => {
          var item = trend.find(d => DISTRIBUTOR_REGION[d.region] === region && d.month === month);
          return item ? item.totalSales : 0;
        })
      };
    });

    // 4️⃣ ECharts 配置
    var option = {
      tooltip: { trigger: 'axis' },
      legend: { data: regions },
      xAxis: { type: 'category', data: months },
      yAxis: { type: 'value' },
      series: series
    };

    myChart.setOption(option);
  }
}

function deleteDistributor(distributorId) {
  if (confirm(`确定要删除分销商 ${distributorId} 吗？`)) {
    console.log('删除分销商:', distributorId);
    alert('分销商删除成功！');
  }
}
