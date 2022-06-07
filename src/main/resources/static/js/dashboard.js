/*
 * Copyright (C) 2022 DiegoMGuillén d761017@hotmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Carga de gráfico al completarse la carga
 */
$(document).ready(init());
//variables globales
var selectedDay = -1;
var selectedMonth = -1;
var selectedYear = -1;
var selectedSniffer = -1;
var hoveredDatasetIndex = -1;
let mes = ["--", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
let dias = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];
let paxday = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1];
let paxhour = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
//inicio y carga
function init() {
  console.log("inicializando panel");
  $("#bt-load").prop('disabled', true);
  $("#load-spinner").hide();
  enableDisableMonthBtnGroup(true);
  enableDisableYearBtnGroup(true);
}
//gráfico linea personas hora
var paxHourChart = new Chart(document.getElementById("paxhourchart"), {
  type: "line",
  data: {
    labels: ["00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"],
    datasets: [{
      label: "Pax",
      fill: true,
      backgroundColor: "transparent",
      borderColor: window.theme.primary,
      data: paxhour
    }]
  },
  options: {
    maintainAspectRatio: false,
    legend: {
      display: false
    },
    tooltips: {
      intersect: false
    }, //
    hover: {
      intersect: true
    },
    plugins: {
      filler: {
        propagate: false
      }
    },
    scales: {
      xAxes: [{
        reverse: true,
        gridLines: {
          color: "rgba(0,0,0,0.05)"
        }
      }],
      yAxes: [{
        ticks: {
          stepSize: 5
        },
        display: true,
        borderDash: [5, 5],
        gridLines: {
          color: "rgba(0,0,0,0)",
          fontColor: "#fff"
        }
      }]
    }
  }
});
//gráfico barras personas mes
var paxDayChart = new Chart(document.getElementById("paxdaychart"), {
  type: "bar",
  data: {
    labels: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"],
    datasets: [{
      label: "Pax",
      backgroundColor: window.theme.primary,
      borderColor: window.theme.primary,
      hoverBackgroundColor: window.theme.primary,
      hoverBorderColor: window.theme.primary,
      data: paxday,
      barPercentage: .75,
      categoryPercentage: .5
    }]
  },
  options: {
    maintainAspectRatio: false,
    legend: {
      display: false
    },
    scales: {
      yAxes: [{
        gridLines: {
          display: false
        },
        stacked: false,
        ticks: {
          stepSize: 20
          //                        callback: function(value, index, ticks) {
          //                        return 'Px' + value;}
        }
      }],
      xAxes: [{
        stacked: false,
        gridLines: {
          color: "transparent"
        }
      }]
    },
    onClick: function(e) {
      let dias = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];
      var activePointLabel = this.getElementsAtEvent(e)[0]._model.label;
      let fecha = new Date(selectedYear, selectedMonth - 1, activePointLabel);
      console.log(dias[fecha.getDay()]);
      updatePaxHourChart(activePointLabel);
    }
  }
});
//manejo de spinner a la carga de la página
$(document).on({
  ajaxStart: function() {
    $("#load-spinner").show();
    console.log("loading");
  },
  ajaxStop: function() {
    $("#load-spinner").hide();
    console.log("loaded");
  }
});
///manejo visual de botones
function enableDisableMonthBtnGroup(state) {
  for (var i = 1; i <= 12; i++) {
    $("#bt-m" + i).prop('disabled', state);
  }
}
///manejo visual de botones
function enableDisableYearBtnGroup(state) {
  $("#bt-y2022").prop('disabled', state);
  $("#bt-y2023").prop('disabled', state);
  $("#bt-y2024").prop('disabled', state);
}
//verificación de que tenemos lo necesario seleccionado para lanzar consultas
function isReadyForRequest() {
  if (selectedMonth != -1 && selectedYear != -1 && selectedSniffer != -1) {
    console.log("readyForRequest");
    updatePaxDayChart();
  } else {
    console.log("not readyForRequest");
  }
}
//obtención del día del mes
function getDayOfTheWeek(dia) {
  let fecha = new Date(selectedYear, selectedMonth - 1, dia);
  return (dias[fecha.getDay()]);
}
//actualización de gráfico
function updatePaxHourChart() {
  $.ajax({
    url: 'rest/devsday/2022/3/23/3',
    type: "GET",
    dataType: "json",
    success: function(result) {
      paxHourChart.data.datasets[0].data = result.slice(0);
      paxHourChart.update();
    }
  });
}
//actualización de gráfico
function updatePaxHourChart(day) {
  $("#day-week").text(getDayOfTheWeek(day) + ", " + day);
  $.ajax({
    url: 'rest/devsday/' + selectedYear + '/' + selectedMonth + '/' + day + '/' + selectedSniffer,
    type: "GET",
    dataType: "json",
    success: function(result) {
      let max = Math.max(...result);
      console.log(max);
      if (max > 0) {
        paxHourChart.data.datasets[0].data = result.slice(0);
        $("#month-day-label").text("Día consultado");
      } else {
        paxHourChart.data.datasets[0].data = paxday.slice(0);
        $("#month-day-label").text("Día consultado sin resultados");
      }
      paxHourChart.update();
      $("#max-day").text(Math.max(...result));
    }
  });
}
//actualización de gráfico
function updatePaxDayChart() {
  $("#month_year").text(mes[selectedMonth] + ":" + selectedYear);
  $.ajax({
    url: 'rest/devsmonth/' + selectedYear + '/' + selectedMonth + '/' + selectedSniffer,
    type: "GET",
    dataType: "json",
    success: function(result) {
      let max = Math.max(...result);
      console.log(max);
      if (max > 0) {
        paxDayChart.data.datasets[0].data = result.slice(0);
        $("#month_year-label").text("Mes y año consultados");
      } else {
        paxDayChart.data.datasets[0].data = paxday.slice(0);
        $("#month_year-label").text("Mes y año consultados sin resultados");
      }
      paxDayChart.update();
      $("#max-month").text(max);
      updatePaxHourChart(1);
    }
  });
}

function setMonth(month) {
  //marcamos activo el boton pulsado
  $("#mes-filter-icon").addClass("text-success");
  $("#mes-txt").addClass("text-success");
  selectedMonth = month;
  isReadyForRequest();
  console.log(selectedMonth);
}

function setYear(year) {
  //marcamos el texto en verde
  $("#ano-filter-icon").addClass("text-success");
  $("#ano-txt").addClass("text-success");
  //
  selectedYear = year;
  isReadyForRequest();
  //si se ha seleccionado mes y sniffer también entoces acivamos el boón de consulta:
  console.log(selectedYear);
}

function setSniffer(sniffer) {
  //marcamos el texto en verde
  $("#snf-filter-icon").addClass("text-success");
  $("#snf-txt").addClass("text-success");
  selectedSniffer = sniffer;
  //activamos botones
  isReadyForRequest();
  console.log(selectedSniffer);
}